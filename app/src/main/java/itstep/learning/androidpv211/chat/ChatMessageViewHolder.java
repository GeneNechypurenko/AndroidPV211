package itstep.learning.androidpv211.chat;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import itstep.learning.androidpv211.R;
import itstep.learning.androidpv211.orm.ChatMessage;

public class ChatMessageViewHolder extends RecyclerView.ViewHolder {
    private final TextView tvAuthor;
    private final TextView tvText;
    private final TextView tvMoment;

    public ChatMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        tvAuthor = itemView.findViewById(R.id.chat_msg_author);
        tvText = itemView.findViewById(R.id.chat_msg_text);
        tvMoment = itemView.findViewById(R.id.chat_msg_moment);
    }

    public void setChatMessage(ChatMessage chatMessage) {
        tvAuthor.setText(chatMessage.getAuthor());
        tvText.setText(chatMessage.getText());
        tvMoment.setText(formatMoment(chatMessage.getMoment()));
    }

    private String formatMoment(Date moment) {
        Calendar now = Calendar.getInstance();
        Calendar msgTime = Calendar.getInstance();
        msgTime.setTime(moment);

        if (now.get(Calendar.YEAR) == msgTime.get(Calendar.YEAR)
                && now.get(Calendar.DAY_OF_YEAR) == msgTime.get(Calendar.DAY_OF_YEAR)) {
            return new SimpleDateFormat("HH:mm", Locale.ROOT).format(moment);
        }

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        if (yesterday.get(Calendar.YEAR) == msgTime.get(Calendar.YEAR)
                && yesterday.get(Calendar.DAY_OF_YEAR) == msgTime.get(Calendar.DAY_OF_YEAR)) {
            return "вчора " + new SimpleDateFormat("HH:mm", Locale.ROOT).format(moment);
        }

        long diffMillis = now.getTimeInMillis() - msgTime.getTimeInMillis();
        long diffDays = diffMillis / (1000 * 60 * 60 * 24);
        if (diffDays < 7) {
            return diffDays + " дн" + (diffDays == 1 ? "ь" : "і") + " тому";
        }

        return new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ROOT).format(moment);
    }
}
