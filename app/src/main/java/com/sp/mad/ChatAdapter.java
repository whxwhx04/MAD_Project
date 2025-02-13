package com.sp.mad;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
    private static final int MESSAGE_TYPE_SENT = 1;
    private static final int MESSAGE_TYPE_RECEIVED = 2;
    private Context context;
    private List<Message> messageList;
    private String currentUserId;
    public ChatAdapter(Context context, List<Message> messageList, String currentUserId) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }
    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        return message.getSenderId().equals(currentUserId) ? MESSAGE_TYPE_SENT : MESSAGE_TYPE_RECEIVED;
    }
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                viewType == MESSAGE_TYPE_SENT ? R.layout.chat_message_sent : R.layout.chat_message_received,
                parent,
                false
        );
        return new MessageViewHolder(view, viewType);
    }
    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.messageText.setText(message.getMessageText());
    }
    @Override
    public int getItemCount() {
        return messageList.size();
    }
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        public MessageViewHolder(View itemView, int viewType) {
            super(itemView);
            messageText = itemView.findViewById(
                    viewType == MESSAGE_TYPE_SENT ? R.id.message_buyer : R.id.message_seller
            );
        }
    }
}