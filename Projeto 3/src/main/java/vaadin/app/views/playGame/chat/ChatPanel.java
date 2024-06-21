package vaadin.app.views.playGame.chat;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;
import rea.components.Character;
import vaadin.app.services.ChatService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;


/**
 * VerticalLayout with the Global Chat Panel
 */
public class ChatPanel extends VerticalLayout {
    Button sendButton = new Button("Send");
    TextField usernameTextField = new TextField();
    TextField msgTextField = new TextField();
    MessageList msgList = new MessageList();
    String userImagePathName;
    Character playerCharacter;
    boolean firstTimeConnecting = true;
    private static ChatService chatService;

    /**
     * Constructor
      * @param cs ChatService
     */
    public ChatPanel(@Autowired ChatService cs) {
        this.chatService = cs;

        VerticalLayout title = new VerticalLayout();
        title.add(new Text("Global"), new Text(" Chat"));
        msgTextField.setHelperText("message");
        HorizontalLayout msgButtons = new HorizontalLayout(title, msgTextField, sendButton);

        // Style
        setMargin(true);
        this.addClassNames("chat-panel");
        msgList.addClassNames("msg-list");
        msgButtons.addClassNames(
                LumoUtility.Margin.NONE,
                LumoUtility.Padding.XSMALL,
                LumoUtility.Gap.XSMALL,
                LumoUtility.Background.BASE,
                LumoUtility.BoxShadow.SMALL,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.Width.FULL,
                LumoUtility.Height.AUTO);

        title.addClassNames(LumoUtility.FontWeight.BOLD,
                LumoUtility.FontSize.SMALL,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.Width.FULL,
                LumoUtility.Height.AUTO);

        add( msgButtons,
                msgList);

        //Implemetação com MessageList

        chatService.register(  chatLine -> {

            ArrayList<MessageListItem> messagesToDisplay = new ArrayList<>();

            for (String message : chatService.chatLine.messageHistory) {
                String[] aux = message.split(" ");
                String senderName = aux[0];

                StringBuilder senderMessage = new StringBuilder();
                for (int i = 2 ; i < aux.length ; i++)
                    senderMessage.append( aux[i] + " ");

                messagesToDisplay.add(
                        createMessageItem(
                                senderName,
                                senderMessage.toString().replace(":","")
                        )
                );
            }
            updateMessageList(messagesToDisplay);
        });

        sendButton.addClickListener(e -> {
            String msg = usernameTextField.getValue() + " : " + msgTextField.getValue();
            chatService.addMessage(msg);
        });

        sendButton.addClickShortcut(Key.ENTER);

    }

    /**
     * Updates msgList.
     * @param msglistitemList List of MessageListItem's.
     */
    private void updateMessageList(List<MessageListItem> msglistitemList) {
        msgList.setItems(msglistitemList);

    }

    /**
     * Creates MessageListItem
     * @param username username who wrote the message
     * @param msg string with message content
     * @return MessageListItem
     */
    private MessageListItem createMessageItem(String username,String msg){
        MessageListItem message = new MessageListItem(msg,
                LocalDateTime.now().toInstant(ZoneOffset.UTC),
                username
        );
        return  message;
    }

    /**
     * Setter method for value of usernameTextField's value
     * @param name string with name of user
     */
    public void setMessageUsername(String name){
        usernameTextField.setValue(name);
    }

    /**
     * Setter method for userImagePathName
     * @param pathname string with pathname fo image
     */
    public  void setUserImagePathname(String pathname){
        userImagePathName = pathname;
    }

    /**
     * Getter method for ChatPanel
     * @return ChatPanel
     */
    public ChatPanel getChatPanel() {
        return this;
    }

    /**
     * Setter method for userImagePathname
     * @param pathname pathname for user image
     */
    public void setUserImage(String pathname) {
        userImagePathName = pathname;
    }
}