# Meeting Thursday 03/03/2022

- Time: 16h00 - 18h45
- Participants: all
- Agenda: discuss frontend dev, HCI final, structure of game


## General Points

- discussing Wiktor and Maarten scenes and menu controllers (some issues with it)
- Maarten: uniqueness constraint on name before inputting to database (bad request)
- App problem Wiktor: not scaling to full screen, suggestion Tudor: use CSS grid pattern and resize event
- css path "@client.css/application.css" but this may not work on mac, however we can "../../client.css/application.css" but this only works on mac not windows
- try instantiating css from java instead of from the fxml files


## HCI fixes

- fixed the details in the description of the issues
- discussed a frequency/impact chart based on the received evaluation
- produced the chart and inserted into the document
- moved the original designs to the introduction/description of the initial prototype
- presented new and improved designs to tackle the most important issues in the conclusion


## Points on structure of the game (classes, etc.)

- Player class in commons and Game classes in the server
- problem right now is we need the questions and activities
- idea: the game classes contain the methods to run a game but game controller manages when things happen (request mappings)
- game service in order to instantiate a new game, put people in a waiting room who click multiplayer