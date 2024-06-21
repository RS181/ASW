package vaadin.app.services;

import org.springframework.stereotype.Service;
import vaadin.app.views.playGame.chat.ChatLine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
public class ChatService {
    public ChatLine chatLine = new ChatLine();

    List<Consumer<ChatLine>> listeners = new ArrayList<>();

    public void addMessage(String message){
        chatLine.addMessage(message);
        broadcast();
    }


    public void register (Consumer<ChatLine> listener){

        listeners.add(listener);

        listener.accept(chatLine);
    }

    void broadcast() {
        for (var iterator = listeners.iterator(); iterator.hasNext();){
            var listener = iterator.next();

            try {
                listener.accept(chatLine);
            } catch (Exception e ){
                iterator.remove();
            }
        }
    }
}
