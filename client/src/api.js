import axios from 'axios'

const SERVER_URL = 'http://localhost:9499'

export const helperFunction = {
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
  changePassword: (user_id, old_password, new_password, repeat_password) => instance.put('/profiles/'+user_id+'/password', {
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
  editProfile: (user_id, firstname, lastname, middlename, nickname, primary_email, bio, date_of_birth, gender, fitness, additional_email, passports) => instance.put('/profiles/'+user_id, {
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
}
