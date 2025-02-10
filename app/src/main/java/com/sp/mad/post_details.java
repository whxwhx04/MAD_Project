package com.sp.mad;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class post_details extends AppCompatActivity {
    private TextView usernameTextView, descriptionTextView, schoolTextView, courseTextView;
    private ImageView postImageView, profileImageView, bck_post;
    private EditText commentEditText;
    private ImageButton sendCommentButton;
    private RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private String postId;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        // Initialize views
        usernameTextView = findViewById(R.id.usernameTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        schoolTextView = findViewById(R.id.schoolTextView);
        courseTextView = findViewById(R.id.courseTextView);
        postImageView = findViewById(R.id.postImageView);
        profileImageView = findViewById(R.id.profileImageView);
        commentEditText = findViewById(R.id.commentEditText);
        sendCommentButton = findViewById(R.id.sendCommentButton);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        bck_post = findViewById(R.id.bck_post);

        // Initialize the comments list and adapter
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentAdapter);

        // Get current user ID
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get the post ID passed from the PostAdapter
        postId = getIntent().getStringExtra("postId");

        if (postId != null && !postId.isEmpty()) {
            // Fetch the post details using the correct postId
            fetchPostDetails();
            fetchComments();
        } else {
            Toast.makeText(this, "Error: Invalid post ID", Toast.LENGTH_SHORT).show();
        }

        // Send comment button functionality
        sendCommentButton.setOnClickListener(v -> {
            String commentText = commentEditText.getText().toString();
            if (!commentText.isEmpty()) {
                sendComment(commentText);
            }
        });

        // Back button functionality
        bck_post.setOnClickListener(v -> {
            // Finish the current activity to return to the previous activity (commPage)
            finish();
        });
    }

    private void fetchPostDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get post details
                        String description = documentSnapshot.getString("description");
                        String imageUrl = documentSnapshot.getString("imageUrl");
                        String userId = documentSnapshot.getString("userId");  // Get the userId of the poster
                        String school = documentSnapshot.getString("school");  // Fetch school
                        String course = documentSnapshot.getString("course");  // Fetch course

                        // Set post image
                        Glide.with(this).load(imageUrl).into(postImageView);

                        // Now fetch the username and profile image from the users collection
                        fetchUserDetails(userId);

                        // Set post description
                        descriptionTextView.setText(description);

                        // Set school and course values
                        schoolTextView.setText(school);
                        courseTextView.setText(course);
                    } else {
                        Toast.makeText(post_details.this, "Post not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(post_details.this, "Error fetching post details: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void fetchUserDetails(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username"); // Fetch username
                        String profileImageUrl = documentSnapshot.getString("profilePicture"); // Fetch profile picture URL

                        // Set username and profile image
                        usernameTextView.setText(username);
                        Glide.with(this).load(profileImageUrl).into(profileImageView);
                    } else {
                        Toast.makeText(post_details.this, "User not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(post_details.this, "Error fetching user details: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void fetchComments() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").document(postId).collection("comments")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        commentList.clear(); // Clear previous comments
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String userId = document.getString("userId");
                            String content = document.getString("content");
                            long timestamp = document.getLong("timestamp");

                            // Add the comment to the list
                            commentList.add(new Comment(userId, content, timestamp));
                        }
                        commentAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(post_details.this, "Error loading comments: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void sendComment(String commentText) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        long timestamp = System.currentTimeMillis();

        // Create a new comment
        Comment comment = new Comment(currentUserId, commentText, timestamp);

        // Add the comment to the Firestore
        db.collection("posts").document(postId).collection("comments")
                .add(comment)
                .addOnSuccessListener(documentReference -> {
                    // Clear the comment input field
                    commentEditText.setText("");
                    Toast.makeText(post_details.this, "Comment added!", Toast.LENGTH_SHORT).show();

                    // Refresh the comments
                    fetchComments();
                })
                .addOnFailureListener(e -> Toast.makeText(post_details.this, "Error adding comment: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
