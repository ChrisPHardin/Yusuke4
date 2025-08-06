package com.example.discordbot;

//import com.neovisionaries.ws.client.ByteArray;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import javax.security.auth.login.LoginException;
import java.util.*;


import java.util.Random;

public class Yusuke extends ListenerAdapter {
    private Map<User, List<Integer>> playerHands = new HashMap<>();
    private Map<User, Integer> dealerHands = new HashMap<>();
    private Map<User, Boolean> inGame = new HashMap<>();
    private String[] responses = {
            "As I see it, yes... or perhaps no? The shadows whisper ambiguously!",
            "Ask again when the moon devours the sun?ah, too dramatic? Try later.",
            "Without a doubt! ...Wait, no, I lied. The doubt is immense.",
            "My sources say 'lol idk' (they are unreliable sources).",
            "Outlook hazy... much like my vision after 3 days without sleep.",
            "As certain as the beauty of a blooming lotus—yes, without doubt.",
            "The winds of fate whisper 'it is decidedly so,' much like the muse guiding an artist’s hand.",
            "Outlook splendid, like a masterpiece unveiled to an adoring crowd.",
            "Alas, the canvas remains blank—reply hazy, try again.",
            "The shadows lengthen, and the answer is a solemn 'no.'",
            "Do not rely upon it, for fortune’s gaze is turned away this day."
    };
    public static void main(String[] arguments) {

        JDA api = JDABuilder.createDefault("", GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(new Yusuke())
                .build();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String apiKey = ""; // Replace with your actual DeepSeek API key
       // ChannelType t = event.getChannelType();
        if (event.getAuthor().isBot()) return;
        // We don't want to respond to other bot accounts, including ourself


        User user = event.getAuthor();
        Message message = event.getMessage();
        String content = message.getContentRaw();

        if (content.startsWith("!8ball")) {
            Random rng = new Random(); // The wheel of fate SPINS!
            int choice = rng.nextInt(responses.length);
            event.getChannel().sendMessage(responses[choice]).queue();
        }

        if (content.equalsIgnoreCase("!blackjack")) {
            if (inGame.getOrDefault(user, false)) {
                event.getChannel().sendMessage("You're already in a game!").queue();
                return;
            }

            inGame.put(user, true);
            playerHands.put(user, new ArrayList<>());
            dealerHands.put(user, 0);

            // Deal initial cards
            drawCard(user);
            drawCard(user);
            dealerHands.put(user, (int) (Math.random() * 11) + 10); // Dealer's initial hand (10-20)

            event.getChannel().sendMessage(
                    "**Your hand:** " + playerHands.get(user) + " (Total: " + getHandValue(user) + ")\n" +
                            "**Dealer shows:** " + dealerHands.get(user) + "\n" +
                            "Type `!hit` to draw or `!stand` to end."
            ).queue();
        }

        if (content.equalsIgnoreCase("!hit")) {
            if (!inGame.getOrDefault(user, false)) {
                event.getChannel().sendMessage("Start a game with `!blackjack` first!").queue();
                return;
            }

            drawCard(user);
            int total = getHandValue(user);

            if (total > 21) {
                event.getChannel().sendMessage(
                        "**Bust!** Your hand: " + playerHands.get(user) + " (Total: " + total + ")"
                ).queue();
                endGame(user, event);
            } else {
                event.getChannel().sendMessage(
                        "**Your hand:** " + playerHands.get(user) + " (Total: " + total + ")"
                ).queue();
            }
        }

        if (content.equalsIgnoreCase("!stand")) {
            if (!inGame.getOrDefault(user, false)) {
                event.getChannel().sendMessage("Start a game with `!blackjack` first!").queue();
                return;
            }

            int playerTotal = getHandValue(user);
            int dealerTotal = dealerHands.get(user);

            // Dealer draws until >= 17
            while (dealerTotal < 17) {
                dealerTotal += (int) (Math.random() * 10) + 1;
            }

            String result;
            if (dealerTotal > 21 || playerTotal > dealerTotal) {
                result = "**You win!**";
            } else if (dealerTotal > playerTotal) {
                result = "**Dealer wins.**";
            } else {
                result = "**Push (tie).**";
            }

            event.getChannel().sendMessage(
                    "**Final Hands**\n" +
                            "You: " + playerHands.get(user) + " (" + playerTotal + ")\n" +
                            "Dealer: " + dealerTotal + "\n" +
                            result
            ).queue();

            endGame(user, event);
        }

        // getContentRaw() is an atomic getter
        // getContentDisplay() is a lazy getter which modifies the content for e.g. console view (strip discord formatting)
        if (content.contains("!ping")) {
            event.getChannel().sendMessage("Pong!").queue(); // Important to call .queue() on the RestAction returned by sendMessage(...)
        } else if (event.getMessage().getMentions().isMentioned(event.getJDA().getSelfUser()) & content.contains("+gocrazy"))  {
            try {
                DeepSeekApiClient deepSeekApi = new DeepSeekApiClient(apiKey);
                //Scanner scanner = new Scanner(System.in);

                // while (true) {
                //System.out.print("Enter your message to DeepSeek (or 'quit' to exit): ");
                String userInput = ("You are Yusuke Kitagawa from Persona 5. Please respond in-character as if you were him. You are also insane and unpredictable, and will respond to things in a confusing, indirect, and nonsensical way. Please ramble crazily in response to the following query or statement: " + content.toLowerCase());

                AskYusuke(event, deepSeekApi, userInput);
                // }

                //scanner.close();
            } catch (Exception e) {
                event.getChannel().sendMessage("durr I'm a dumbass: " + e.getMessage()).queue();
            }
        } else if (event.getMessage().getMentions().isMentioned(event.getJDA().getSelfUser()))  {
            try {
                DeepSeekApiClient deepSeekApi = new DeepSeekApiClient(apiKey);
                //Scanner scanner = new Scanner(System.in);

               // while (true) {
                    //System.out.print("Enter your message to DeepSeek (or 'quit' to exit): ");
                    String userInput = ("You are Yusuke Kitagawa from Persona 5. Please respond in-character as if you were him. Respond to the following query or statement: " + content.toLowerCase());

                AskYusuke(event, deepSeekApi, userInput);
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
                    event.getChannel().sendMessage("https://media.tenor.com/WpQU9sQGHfkAAAAM/yusuke-persona5.gif").queue();
                } else if (randomNumber == 4) {
                    event.getChannel().sendMessage("https://media0.giphy.com/media/v1.Y2lkPTZjMDliOTUyZGJ4eTcyemYxMzJ0bXQwbDJ0NTlrNGN4eDZkOTA1NXVsNXNmZmNlNSZlcD12MV9naWZzX3NlYXJjaCZjdD1n/z6TMaaNJKIAX6/source.gif").queue();
                   // event.getChannel().sendMessage("https://media.tenor.com/Ygr-K-GVRTMAAAAM/goku-dragon-ball.gif").queue();
                } else if (randomNumber == 5) {
                    event.getChannel().sendMessage("I LOOOOVE BETA").queue();
                } else if (randomNumber == 6) {
                    event.getChannel().sendMessage("https://i.pinimg.com/originals/1a/43/2d/1a432d360ad65a2dc04a568e6b99b433.gif").queue();
                    //event.getChannel().sendMessage("I LOOOOVE DANGO").queue();
                } else if (randomNumber == 7) {
                    try {
                        DeepSeekApiClient deepSeekApi = new DeepSeekApiClient(apiKey);
                        //Scanner scanner = new Scanner(System.in);

                        // while (true) {
                        //System.out.print("Enter your message to DeepSeek (or 'quit' to exit): ");
                        String userInput = ("You are Yusuke Kitagawa from Persona 5. Please respond in-character as if you were him. Respond to the following previous message from " + event.getAuthor().toString() + ": " + content.toLowerCase());

                        AskYusuke(event, deepSeekApi, userInput);
                        // }

                        //scanner.close();
                    } catch (Exception e) {
                        event.getChannel().sendMessage("durr I'm a dumbass: " + e.getMessage()).queue();
                    }
                } else if (randomNumber == 8) {
                    event.getChannel().sendMessage("https://i.redd.it/zkdreo021a961.gif").queue();
                } else if (randomNumber == 9) {
                    event.getChannel().sendMessage("https://i.pinimg.com/originals/0f/0d/29/0f0d29ab1469887f497aef20079e987b.gif").queue();
                } else {
                    event.getChannel().sendMessage("https://media.tenor.com/el4qYKk5PpkAAAAM/yusuke-test-gif.gif").queue();
                }
            }
        }
    }

    private void AskYusuke(@NotNull MessageReceivedEvent event, DeepSeekApiClient deepSeekApi, String userInput) {
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
    }

    private void drawCard(User user) {
        int card = (int) (Math.random() * 10) + 1; // 1-10
        playerHands.get(user).add(card);
    }

    private int getHandValue(User user) {
        return playerHands.get(user).stream().mapToInt(Integer::intValue).sum();
    }

    private void endGame(User user, MessageReceivedEvent event) {
        playerHands.remove(user);
        dealerHands.remove(user);
        inGame.remove(user);
    }

}

