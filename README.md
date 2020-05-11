"# emoji-app" 
=======
# emoji-app
Emotions through text program for Penn's Cultural Evolution of Language Lab

How to run:

(1) Run Server.java (in the server package). This should print "Server has started..." in the Terminal.

(2) Run Sender.java (in clients --> sender). Should see a screen with an overlay that says "Waiting for receiver...".

(3) Run Receiver.java (in clients --> receiver). Now both clients have connected.

(4) The Sender can now watch or replay the video, send a message to the receiver, and input the emotion/confidence info. Then hit "Done."

(5) The Sender waits for the Receiver's data.

(6) From the Receiver's screen, input the emotion/confidence info, then hit "Done."

(7) Then, on the Sender's screen will be the data the Receiver entered in, shown for a few seconds. Their screen then resets to the next
round, with a new video.

(8) On the Receiver's screen will be the video the Sender viewed, played automatically. Their screen then resets to the next round.

(9) This repeats for each round, until there are no videos left to display (from data -> test_videos).

(10) On the last round, the sender and receiver will see a "Game over!" screen. After clicking "Quit" an exit form will appear.

(11) After filling it out and clicking "Submit," the results of the game (incl. the exit form data) will appear in the results file 
(data --> out.txt). See out.txt to see an example.
