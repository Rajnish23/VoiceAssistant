package com.speech.voiceassistant.adapter;

import android.view.LayoutInflater;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;


import com.speech.voiceassistant.data.VoiceChat;
import com.speech.voiceassistant.databinding.ChatItemBinding;


import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<VoiceChat> chatList;

    public ChatAdapter(List<VoiceChat> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChatItemBinding chatItemBinding = ChatItemBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new ChatViewHolder(chatItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        final VoiceChat msg = chatList.get(position);
        holder.bind(msg);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        private ChatItemBinding mBinding;
        public ChatViewHolder(@NonNull ChatItemBinding itemView) {
            super(itemView.getRoot());
            mBinding = itemView;
        }

        public void bind(VoiceChat msg) {
            mBinding.setMessage(msg);
            mBinding.executePendingBindings();
        }
    }
}
