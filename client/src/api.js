import axios from 'axios'

const SERVER_URL = 'http://localhost:9499'

//Might need if cookies break again
// function getCookie(name){
//   var value = "; "+document.cookie;
//   var parts = value.split("; "+name+"=");
//   if (parts.length == 2) return parts.pop().split(";").shift();
// }

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
  withCredentials: true,
  //headers: {'Authorization': getCookie("s_id")}
});

export const apiUser = {
  // Update the user's password
  changePassword: (user_id, old_password, new_password, repeat_password) => instance.put('/profiles/'+user_id+'/password', {
    old_password: old_password,
    new_password: new_password,
    repeat_password: repeat_password
  }),
  // Submit user signup information to the server
  signUp: (firstname, lastname, middlename, nickname, primary_email, password, bio, date_of_birth, gender, fitnessLevel) => instance.post('/profiles', {
    firstname: firstname,
    lastname: lastname,
    middlename: middlename,
    nickname: nickname,
    primary_email: primary_email,
    password: password,
    bio: bio,
    date_of_birth: date_of_birth,
    gender: gender,
    fitnessLevel: fitnessLevel
  }),
  logout: () => instance.post('/logout').then(
    function () {
      document.cookie = "s_id = ; expires = Thu, 01 Jan 1970 00:00:00 GMT"
    })
}
