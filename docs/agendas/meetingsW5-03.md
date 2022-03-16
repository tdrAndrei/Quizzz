# Meeting Saturday 12th March

- Time: 17h45 - 18h30
- Participants: all 
- Main theme: Disucss week's progress

## Server side

- what jokers are we doing for solo?
- for checking the correct answer: is there another way other than making a getter to inform the user of a correct answer
- maybe we could not serialiaze the attribute so it's not sent on json (because rn its being sent in plain text)
- some testing is hard because it is time dependent
- note stackoverflow error after 1 or 2 mins in game, let's see if we can improve on this 
- sometimes requests do not get answered (expire) but then registered as if it was sent -> could be fatal error in terms of sending questions/answers
- design choice: we made only one game class; in multi if one player left (everyone else disconnected) right now it becomes solo
- we should probably make a boolean to store the score or not

## Client side

- resizing of the css is pretty much done + some animations are in place
- next week we establish the connection with the server: display the actual questions, make the communication
- some 3 people on the fxml and controllers, some other 3 on the communication
- we still need to make the estimate question screen: time bar (empty), slider, labels to display the answer but nothing in them

## End of Sprint

- finish the estimate Q screen
- resizing works, but we need to maybe resize elements (they stay very small) -> probably for next week
- block window resize below a certain number
- then merge everything to main
- meet on monday for next week's task division
- think about: how will connection work with the server form client's perspective (polling, get from scene to scene, etc.)
