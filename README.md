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
Name: SENG302_DB_URL Value: SENG302_DB_URL from git <br />
Name: SENG302_DB_USERNAME Value: SENG302_DB_USERNAME from git <br />
Name: SENG302_DB_PASSWORD Value: SENG302_DB_PASSWORD from git
8. Right-click Main.java in `src/main/java/com.springvuegradle.Hakinakina`, and select Run 'Main'.

###To run the client: 

####Option 1:
1. Open WebStorm
2. Select Open. 
3. Select the `team-300/client/` folder and click OK.
4. Create a new npm run configuration with:
    * `client/package.json` as the package.json
    * `run` as the command
    * `serve` as the scripts
    * `GOOGLE_MAPS_KEY=GOOGLE_MAPS_KEY` under environment where GOOGLE_MAPS_KEY is the git environment variable
5. Save the configuration
5. In the terminal, enter `npm install`
6. Run the created configuration
5. Open http://localhost:9500/ in a browser, where you will be taken to the landing page

**Please have the server and client running at the same time while using the application.**
Our manual test document can be found on our GitLab Wiki in the [Manual Testing page](https://eng-git.canterbury.ac.nz/seng302-2020/team-300/wikis/manual-testing).

##Example Login Details

#####Regular User:
Email: `smalljoe@gmail.com`
Password: `Qq123456`

#####Admin User:
Email: `bigjoe@gmail.com`
Password: `Ww123456`

#####Default Admin:
Email: `passageAdmin@gmail.com`
Password: `iD0nDppp62I4gkp`

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
