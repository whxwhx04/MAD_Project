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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class post_details extends AppCompatActivity {
    private TextView usernameTextView, descriptionTextView, schoolTextView, courseTextView;
    private ImageView postImageView, profileImageView, bck_post, deletePost;
    private EditText commentEditText;
    private ImageButton sendCommentButton;
    private RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private String postId;
    private String currentUserId;

    // Firestore and FirebaseStorage instances
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

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
        deletePost = findViewById(R.id.deletepost);

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
        bck_post.setOnClickListener(v -> finish());

        // Delete post functionality
        deletePost.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("posts").document(postId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String userId = documentSnapshot.getString("userId");  // Get userId of the post owner
                            if (currentUserId.equals(userId)) {
                                // User is the owner of the post, delete the post
                                deletePost();
                            } else {
                                // User is not the owner
                                Toast.makeText(post_details.this, "Sorry, you are not the owner of this post", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(post_details.this, "Post not found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(post_details.this, "Error checking post ownership: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    private void fetchPostDetails() {
        db.collection("posts").document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String description = documentSnapshot.getString("description");
                        String imageUrl = documentSnapshot.getString("imageUrl");
                        String userId = documentSnapshot.getString("userId");  // Get the userId of the poster
                        String school = documentSnapshot.getString("school");
                        String course = documentSnapshot.getString("course");

                        Glide.with(this).load(imageUrl).into(postImageView);
                        fetchUserDetails(userId);

                        descriptionTextView.setText(description);
                        schoolTextView.setText(school);
                        courseTextView.setText(course);
                    } else {
                        Toast.makeText(post_details.this, "Post not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(post_details.this, "Error fetching post details: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void fetchUserDetails(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        String profileImageUrl = documentSnapshot.getString("profilePicture");

                        usernameTextView.setText(username);
                        Glide.with(this).load(profileImageUrl).into(profileImageView);
                    } else {
                        Toast.makeText(post_details.this, "User not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(post_details.this, "Error fetching user details: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void fetchComments() {
        db.collection("posts").document(postId).collection("comments")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        commentList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String userId = document.getString("userId");
                            String content = document.getString("content");
                            long timestamp = document.getLong("timestamp");

                            commentList.add(new Comment(userId, content, timestamp));
                        }
                        commentAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(post_details.this, "Error loading comments: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void sendComment(String commentText) {
        long timestamp = System.currentTimeMillis();
        Comment comment = new Comment(currentUserId, commentText, timestamp);

        db.collection("posts").document(postId).collection("comments")
                .add(comment)
                .addOnSuccessListener(documentReference -> {
                    commentEditText.setText("");
                    Toast.makeText(post_details.this, "Comment added!", Toast.LENGTH_SHORT).show();
                    fetchComments();
                })
                .addOnFailureListener(e -> Toast.makeText(post_details.this, "Error adding comment: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void deletePost() {
        DocumentReference postRef = db.collection("posts").document(postId);

        // Fetch the image URL before deleting the post
        postRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                String imageUrl = document.getString("imageUrl");

                // Get reference to the image file in Firebase Storage
                StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);

                // Delete the image from Firebase Storage
                imageRef.delete().addOnCompleteListener(imageDeleteTask -> {
                    if (imageDeleteTask.isSuccessful()) {
                        // After image is deleted, delete the post from Firestore
                        postRef.delete().addOnCompleteListener(deleteTask -> {
                            if (deleteTask.isSuccessful()) {
                                // Show Toast message on success
                                Toast.makeText(post_details.this, "Post and image deleted successfully", Toast.LENGTH_SHORT).show();

                                // Navigate back to previous activity after deletion
                                finish();
                            } else {
                                // Show error message if deletion fails
                                Toast.makeText(post_details.this, "Error deleting post", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Show error message if image deletion fails
                        Toast.makeText(post_details.this, "Error deleting image", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(post_details.this, "Error fetching post data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
