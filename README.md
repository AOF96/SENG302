# Passeage App

Welcome to Passage, the number one companion for any traveller. 

## How to run

To run the server, open IntelliJ. Then select Import Project. The server folder can be found in team-300. Select this folder and click OK. In the Import Project window that opens, select "Import project from external model", and select Gradle. Click Finish, and the project will open where the dependencies should install automatically. Right-click Main in src/main/java/com.springvuegradle.Hakinakina, and select Run 'Main'.

Alternatively, run the command "./gradlew bootRun" in the server directory.

To run the client, open WebStorm and select Open. Select the team-300 folder and click OK. In the terminal, enter the following commands:
cd client
npm install
npm run serve

Open http://localhost:9500/ in a browser, where you will be taken to the login page.

Please have the server and client running at the same time while using the application. Our manual test document can be found on our GitLab Wiki in the Manual Testing page.

## Page structure

- As a normal user, you can login or signup and then go to our system.
- As a default admin user with email "passageAdmin@gmail.com" and password "368FkAo3ckf4HN4", you can login and search user by id. Then you will go to our system as the normal user you searched.

![page_structure](/uploads/a6b190030d901a8574176e1e64811922/page_structure.png)

- Jackie Qiu
- Ryosuke Okamae
