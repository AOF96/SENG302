import axios from 'axios'

const SERVER_URL = 'http://localhost:9499'

export const helperFunction = {
  // Assigns a new cookie to a given user.
  addCookie: (cname, cvalue, exdays) => {
    var d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    var expires = "expires=" + d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
  }
}

const instance = axios.create({
  baseURL: SERVER_URL,
  timeout: 5000,
  withCredentials: true
});

export const apiUser = {
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
  // Removes session cookie and posts server request to remove the token from the database
  logout: () => instance.post('/logout').then(function () {
    document.cookie = "s_id = ; expires = Thu, 01 Jan 1970 00:00:00 GMT";
  }),
  // Submit user signup information to the server
  editProfile: (profile_id, firstname, lastname, middlename, nickname, primary_email, bio, date_of_birth, gender, fitness, additional_email, passports, activities) => instance.put('/profiles/'+profile_id, {
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
    passports: passports,
    activities: activities
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

  /**
   * Request to update activity types
   */
  editUserActivityTypes: (profile_id, activities) => instance.put('/profiles/'+ profile_id + '/activity-types', {
    activities: activities
  }),

  /**
   * Request to get all activity types from the server
   */
  getActivityTypes: () => instance.get('/activity-types')
};
