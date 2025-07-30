package com.example.discordbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.Random;

public class Yusuke extends ListenerAdapter {
    public static void main(String[] arguments) {
        JDA api = JDABuilder.createDefault("", GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(new Yusuke())
                .build();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String apiKey = ""; // Replace with your actual DeepSeek API key
       // ChannelType t = event.getChannelType();
        if (event.getAuthor().isBot()) return;
        // We don't want to respond to other bot accounts, including ourself

        String content = event.getMessage().getContentRaw();
        // getContentRaw() is an atomic getter
        // getContentDisplay() is a lazy getter which modifies the content for e.g. console view (strip discord formatting)
        if (content.contains("!ping")) {
            event.getChannel().sendMessage("Pong!").queue(); // Important to call .queue() on the RestAction returned by sendMessage(...)
        } else if (event.getMessage().getMentions().isMentioned(event.getJDA().getSelfUser())) {
            try {
                DeepSeekApiClient deepSeekApi = new DeepSeekApiClient(apiKey);
                //Scanner scanner = new Scanner(System.in);

               // while (true) {
                    //System.out.print("Enter your message to DeepSeek (or 'quit' to exit): ");
                    String userInput = ("You are Yusuke Kitagawa from Persona 5. Please respond in-character as if you were him. Respond to the following query or statement: " + content.toLowerCase());

                        deepSeekApi.sendMessageWithStreaming(userInput, false, new DeepSeekApiClient.StreamCallback() {
                            @Override
                            public void onMessage(String message) {
                                event.getChannel().sendMessage(message).queue();
                            }

                            @Override
                            public void onError(String error) {
                                event.getChannel().sendMessage("Error: " + error).queue();
                            }

                            @Override
                            public void onComplete() {
                               // event.getChannel().sendMessage("--- Response complete ---").queue();
                            }
                        });
                   // }

                //scanner.close();
            } catch (Exception e) {
                event.getChannel().sendMessage("durr I'm a dumbass: " + e.getMessage()).queue();
            }
        } else if (content.contains(" ")) {
            Random random = new Random();
            int randomNumber = random.nextInt(300);
            if (randomNumber < 10) {
                if (randomNumber == 1) {
                    event.getChannel().sendMessage("PEERSONAA DIOOO!!!!").queue(); // Important to call .queue() on the RestAction returned by sendMessage(...)
                } else if (randomNumber == 2) {
                    event.getChannel().sendMessage("FUCK YOU AHHAHHHHHHASHDHAHHHH").queue();
                } else if (randomNumber == 3) {
                    event.getChannel().sendMessage("ٱلرِّجَالُ\n" + "٣٤").queue();
                } else if (randomNumber == 4) {
                    event.getChannel().sendMessage("Starfire will pull me soon!").queue();
                   // event.getChannel().sendMessage("https://media.tenor.com/Ygr-K-GVRTMAAAAM/goku-dragon-ball.gif").queue();
                } else if (randomNumber == 5) {
                    event.getChannel().sendMessage("I LOOOOVE REXX").queue();
                } else if (randomNumber == 6) {
                    event.getChannel().sendMessage("https://i.pinimg.com/originals/1a/43/2d/1a432d360ad65a2dc04a568e6b99b433.gif").queue();
                    //event.getChannel().sendMessage("I LOOOOVE DANGO").queue();
                } else if (randomNumber == 7) {
                    event.getChannel().sendMessage("https://64.media.tumblr.com/b2be2a45416439b3668fd2f92f6038a0/857f68bad349f32e-ec/s400x600/0cc96dfc3c8a091d989cade894a0064147a98d1b.gifv").queue();
                } else if (randomNumber == 8) {
                    event.getChannel().sendMessage("https://i.redd.it/zkdreo021a961.gif").queue();
                } else if (randomNumber == 9) {
                    event.getChannel().sendMessage("https://64.media.tumblr.com/c1ecb720e21f675873b2a5ea31de45b7/857f68bad349f32e-9b/s400x600/88d72d3e32f0aaf000c76ab167653e3d40a046ff.gifv").queue();
                } else {
                    event.getChannel().sendMessage("https://media.tenor.com/el4qYKk5PpkAAAAM/yusuke-test-gif.gif").queue();
                }
            }
        }
    }

}

