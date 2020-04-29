import axios from 'axios';

const SERVER_URL = 'http://localhost:9499';

var instance = axios.create({
  baseURL: SERVER_URL,
  timeout: 5000,
  headers: {'token': localStorage.s_id}
});

export const apiUser = {
  // Reload token from local storage
  refreshInstance: () => {
    instance = axios.create({
      baseURL: SERVER_URL,
      timeout: 5000,
      headers: {'token': localStorage.s_id}
    });
  },
  // Update the user's password
  changePassword: (profile_id, old_password, new_password, repeat_password) => instance.put('/profiles/'+profile_id+'/password', {
    old_password: old_password,
    new_password: new_password,
    repeat_password: repeat_password
  }),
  // Submit user signup information to the server
  signUp: (firstname, lastname, middlename, nickname, primary_email, password, bio, date_of_birth, gender, fitness) => instance.post('/profiles', {
    firstname: firstname,
    lastname: lastname,
    middlename: middlename,
    nickname: nickname,
    primary_email: primary_email,
    password: password,
    bio: bio,
    date_of_birth: date_of_birth,
    gender: gender,
    fitness: fitness
  }),
  // Submit user login request to the server
  login: (email, password) => instance.post('/login', {
    email: email,
    password: password
  }),
  // Removes session token from local storage and posts server request to remove the token from the database
  logout: () => instance.post('/logout'),
  // Submit user signup information to the server
  editProfile: (profile_id, firstname, lastname, middlename, nickname, primary_email, bio, date_of_birth, gender, fitness, additional_email, passports) => instance.put('/profiles/'+profile_id, {
    firstname: firstname,
    lastname: lastname,
    middlename: middlename,
    nickname: nickname,
    primary_email: primary_email,
    bio: bio,
    date_of_birth: date_of_birth,
    gender: gender,
    fitness: fitness,
    additional_email: additional_email,
    passports: passports
  }),
  refreshUserData: (profile_id) => instance.get('/profiles/' + profile_id),
  // Add additional emails
  addEmails: (profile_id, additional_email) => instance.post('/profiles/'+profile_id+'/emails', {
    additional_email: additional_email
  }),
  // Edit the user's emails
  editEmail: (profile_id, primary_email, additional_email) => instance.put('/profiles/'+profile_id+'/emails', {
    primary_email: primary_email,
    additional_email: additional_email
  }),
  //Get all emails
  getAllEmails: () => instance.get('/emails'),

  getUserSessionToken: (profile_id)  => instance.get('/token/' + profile_id),

  getUserByToken: () => instance.get('validateLogin'),

  async getUserById(profile_id) {
    let searchedUser = await apiUser.refreshUserData(profile_id).then(
      response => {
        return response.data;
      },
      error => {
        if(error){
          return "Invalid permissions";
        }
      }
    );
    return await searchedUser;
  }
};
