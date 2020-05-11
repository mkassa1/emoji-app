package game;

public class Instructions {

    public static String SENDER_INSN = "Welcome to the Emoji Game! This game is intended to measure how well you and a " +
            "partner can read, understand, and measure emotion through text. As the Sender, the object of each round will " +
            "be to convey to your partner, the Receiver, the emotion you think is being conveyed in a short video you have " +
            "watched. On your screen will be the following items: a chat interface for messaging between you and the " +
            "receiver, a video player, and an input region where you will enter (1) the emotion you think is being " +
            "conveyed in the video, (2) how strong the emotion is, (3) your confidence that the receiver will choose this same emotion, " +
            "and (4) your confidence that the receiver will choose the same emotion strength. At each round, " +
            "follow these steps:" +
            "\n----------------------------------------------------------------------" +
            "\n(1) Watch the video -- you may replay it if you like." +
            "\n(2) In the dropdown, enter the emotion you feel is being conveyed." +
            "\n(3) In each of the 3 sliders, enter your confidence levels in the emotion you chose and the emotion strength." +
            "\n(4) Type out the sentence read out to you in the video. Send this as a message to the receiver in a way that " +
            "you feel best conveys the emotion you selected. Note: you may use emojis!"
            + "\n(5) Click Done."
            + "\nOnce the receiver has entered their guess, at the end of the round you will be shown their chosen " +
            "emotion, emotion strength, and confidence. Remember, the object of the game is to send the receiver a " +
            "message that best conveys the emotion in the video!"
            + "\nGood luck!";

    public static String RECEIVER_INSN = "Welcome to the Emoji Game! This game is intended to measure how well you and a " +
            "partner can read, understand, and measure emotion through text. As the Receiver, the object of each round will " +
            "be to guess the emotion your partner is trying to convey through a text message. At the beginning of the round, " +
            "the Sender will watch a video of a sentence being stated. The Sender will then send you a message best conveying "+
            "the emotion in the video they watched. On your screen will be the following items: a chat interface for messaging " +
            "between you and the sender, a video player area, and an input region where you will enter (1) the emotion you think is being " +
            "conveyed in the text message, (2) how strong you think the emotion is based on the message, (3) your confidence " +
            "that you chose the correct emotion, and (4) your confidence that the you chose the correct emotion strength. At each round, " +
            "follow these steps:" +
            "\n----------------------------------------------------------------------" +
            "\n(1) Read the Sender's text message. Note that it may include emojis." +
            "\n(2) In the dropdown, enter the emotion you feel is being conveyed in the message." +
            "\n(3) In each of the 3 sliders, enter your confidence levels in the emotion you chose and the emotion strength." +
            "\n(4) Click Done."
            + "\nOnce you submit your guesses, at the end of the round you will be shown the video the Sender watched. Remember, " +
            "the object of the game is to use clues from the Sender's text message to guess the emotion being conveyed!"
            + "\nGood luck!";
}
