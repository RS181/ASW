package vaadin.app.views.playGame.chat;

import java.util.ArrayList;
import java.util.List;

public class ChatLine {

    List<String> messageHistory = new ArrayList<>();

    public void addMessage(String message){
        messageHistory.add(message);
    }


    List<String> getMessageHistory (){
        return  messageHistory;
    }


}
