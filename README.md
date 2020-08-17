# Passage App

Welcome to Passage, the number one companion for any traveller. 

## How to run

###To run the server:

####Option 1:
1. Open IntelliJ. 
2. Select Import Project. 
3. Select the `team-300/server/` folder and click OK.
4. In the Import Project window that opens, select "Import project from external model", and select Gradle. 
5. Click Finish, and the project will open where the dependencies should install automatically. 
6. In the top right of IntelliJ select the box "Main" and select "Edit configurations...".
7. Under override parameters using the + button on the right add 3 new entries with the values shown below.<br />
Name: SENG302_DB_URL Value: jdbc:mariadb://127.0.0.1/seng302-2020-team300-test <br />
Name: SENG302_DB_USERNAME Value: seng302-team300 <br />
Name: SENG302_DB_PASSWORD Value: CreatingRibbon7031
8. Right-click Main.java in `src/main/java/com.springvuegradle.Hakinakina`, and select Run 'Main'.

####Option 2:
1. Open `team-300/server/` directory in console.
2. Run the command `./gradlew bootRun`

###To run the client: 

####Option 1:
1. Open WebStorm
2. Select Open. 
3. Select the `team-300/client/` folder and click OK.
4. In the terminal, enter the following commands in order:
```
npm install
npm run serve
```
5. Open http://localhost:9500/ in a browser, where you will be taken to the login page.

####Option 2:
1. Open `team-300/client/` directory in console.
2. Enter the following commands in order:
```
npm install
npm run serve
```

**Please have the server and client running at the same time while using the application.**
Our manual test document can be found on our GitLab Wiki in the Manual Testing page.

##Example Login Details

#####Regular User:
Email: `smalljoe@gmail.com`
Password: `Qq123456`

#####Admin User:
Email: `bigjoe@gmail.com`
Password: `Ww123456`

#####Default Admin:
Email: `passageAdmin@gmail.com`
Password: `949LtDmcndy59fw`

##Licensing/Credits

- Geographical data is retrieved from [Open Street Map](https://www.openstreetmap.org/) using [Photon](https://photon.komoot.de/)
- Country data retrieved from [REST Countries API](https://restcountries.eu/)

##Authors

- Mayuko Williams
- Jackie Qiu
- Samuel Sandri
- Anzac Morel
- Frankie Oprenario
- Karna Malone
- Shivin Gaba
- Adrian Osuna Fregoso
- Ryosuke Okamae
