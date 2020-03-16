import axios from 'axios'

const SERVER_URL = 'http://localhost:9499'

function getCookie(name){
  var value = "; "+document.cookie;
  var parts = value.split("; "+name+"=");
  if (parts.length == 2) return parts.pop().split(";").shift();
}

const instance = axios.create({
  baseURL: SERVER_URL,
  timeout: 5000,
  withCredentials: true,
  headers: {'Authorization': getCookie("s_id")}
});

export const apiUser = {
  // Update the user's password
  changePassword: (user_id, old_password, new_password, repeat_password) => instance.put('/profiles/'+user_id+'/password', {
    old_password: old_password,
    new_password: new_password,
    repeat_password: repeat_password
  })
}
