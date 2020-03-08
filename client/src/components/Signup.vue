<template>
<div>
  <NavBar />
  <div class="credentials-box-wrap">
    <div id="credentials-box">
      <h1>Sign Up</h1>
      <h2>Create an account</h2>
      <form @submit.prevent>
        <div class="signup-row">
          <input class="flName" v-model="user.fname" name="fname" type="text" placeholder="First Name*" required>
          <input class="flName" v-model="user.lname" name="lname" type="text" placeholder="Last Name*" required>
        </div>
        <div class="signup-row">
          <input v-model="user.nickname" name="nickname" type="text" placeholder="Nickname">
          <select v-model="user.gender" name="gender" placeholder="Gender" value="Gender" required>
            <option selected disabled hidden>Gender</option>
            <option>Non-Binary</option>
            <option>Female</option>
            <option>Male</option>
          </select>
        </div>
        <div class="signup-row">
          <input v-model="user.email" class="signupInput-email" name="email" type="email" placeholder="Email*" required>
        </div>
        <div class="signup-row">
          <h3 id="signupText-birthday">Birthday</h3>
          <input v-model="user.birthday" class="signupInput-birthday" name="birthday" type="date" placeholder="Birthday" required>
        </div>
        <div class="signup-row">
        <h3 id="fitnessLevelText">Fitness Level</h3>
        <select id="levels">
          <option value="volvo">0</option>
          <option value="saab">1</option>
          <option value="opel">2</option>
          <option value="audi">3</option>
          <option value="audi">4</option>
          <option value="audi">5</option>
        </select>
        </div>
        <div class="signup-row">
          <input v-model="user.password1" class="signupInput-password" name="pass1" type="password" placeholder="Password*" required>
        </div>
        <div class="signup-row">
          <input v-model="user.password2" class="signupInput-password" name="pass2" type="password" placeholder="Password Again*" required>
        </div>
        <ul class="validation-errors">
          <li v-if="!validation.password.match">
            {{ this.err_msg.password.match }}
          </li>
          <li v-if="!validation.password.length">
            {{ this.err_msg.password.length }}
          </li>
          <li v-if="!validation.password.number">
            {{ this.err_msg.password.number }}
          </li>
          <li v-if="!validation.password.lowercase">
            {{ this.err_msg.password.lowercase }}
          </li>
          <li v-if="!validation.password.uppercase">
            {{ this.err_msg.password.uppercase }}
          </li>
        </ul>
        <hr>
        <input v-on:click="submitSignUp()" id="signupButton-submit" type="submit" value="Sign Up">
      </form>
    </div>
    <h4>Already have an account? <router-link to="/Login">Login</router-link>
    </h4>
  </div>
</div>
</template>

<script>
import axios from 'axios'
import router from "../router";
import {
  getEncryptPassword
} from "../common.js"
const SERVER_URL = 'https://4967d4f4-8301-42d1-a778-e3d150633644.mock.pstmn.io'

import NavBar from '@/components/NavBar'
import {
  userInfo
} from '../globals';

const ERR_MSG_FNAME = 'Please enter your First name'
const ERR_MSG_LNAME = 'Please enter your Last name'
const ERR_MSG_GENDER = 'Please select your Gender'
const ERR_MSG_EMAIL = 'Please enter a valid Email'
const ERR_MSG_BIRTHDAY = 'Please select your Birthday'
const ERR_MSG_PASS_MATCH = 'Password must match'
const ERR_MSG_PASS_NUMBER = 'Password must include at least 1 number'
const ERR_MSG_PASS_LENGTH = 'Password must be longer than 8 characters'
const ERR_MSG_PASS_LOWERCASE = 'Password must include lowercase characters'
const ERR_MSG_PASS_UPPERCASE = 'Password must include uppercases characters'
const DEFAULT_ALL_ERR_MSG = 'Please fill all required inputs\n'


export default {
  name: 'Signup',
  components: {
    NavBar
  },
  data() {
    return {
      user: {
        fname: '',
        lname: '',
        nickname: '',
        gender: 'Gender',
        email: '',
        birthday: '',
        password1: '',
        password2: '',
        message: '',
      },
      err_msg: {
        fname: ERR_MSG_FNAME,
        lname: ERR_MSG_LNAME,
        gender: ERR_MSG_GENDER,
        email: ERR_MSG_EMAIL,
        birthday: ERR_MSG_BIRTHDAY,
        password: {
          match: ERR_MSG_PASS_MATCH,
          number: ERR_MSG_PASS_NUMBER,
          length: ERR_MSG_PASS_LENGTH,
          lowercase: ERR_MSG_PASS_LOWERCASE,
          uppercase: ERR_MSG_PASS_UPPERCASE,
        },
      },
      failed: false
    }
  },

  computed: {
    validation() {
      return {
        fname: this.user.fname != '',
        lname: this.user.lname != '',
        gender: this.user.gender != 'Gender',
        email: /[^\s]+@[^\s]+/.test(this.user.email),
        birthday: this.user.birthday != '',
        password: {
          match: this.user.password1 == this.user.password2,
          length: /.{8,}/.test(this.user.password1),
          number: /\d/.test(this.user.password1),
          lowercase: /[a-z]/.test(this.user.password1),
          uppercase: /[A-Z]/.test(this.user.password1),
        },
      }
    },

    all_err_msg() {
      const validation = this.validation
      const fields = Object.keys(validation)

      let err_msg = DEFAULT_ALL_ERR_MSG
      for (let i in fields) {
        const field = fields[i]
        if (!validation[field]) {
          err_msg += '\n'
          err_msg += this.err_msg[field]
        }
        const keys = Object.keys(validation[field])
        for (let i in keys) {
          const key = keys[i]
          if (!validation[field][key]) {
            err_msg += '\n'
            err_msg += this.err_msg[field][key]
          }
        }
      }
      return err_msg
    },

    valid() {
      const valid = (this.all_err_msg == DEFAULT_ALL_ERR_MSG)
      return valid
    }
  },

  methods: {
    init() {
      this.user.fname = ''
      this.user.lname = ''
      this.user.nickname = ''
      this.user.gender = 'Gender'
      this.user.email = ''
      this.user.birthday = ''
      this.user.password1 = ''
      this.user.password2 = ''
    },

    submitSignUp() {
      if (!this.valid) {
        alert(this.all_err_msg)
        this.failed = true
        return
      }

      userInfo.isLogin = true;
      userInfo.firstname = this.user.fname;
      userInfo.lastname = this.user.lname;
      userInfo.nickname = this.user.nickname;
      userInfo.gender = this.user.gender;
      userInfo.email = this.user.email;
      userInfo.birthday = this.user.birthday;

      axios.post(SERVER_URL + '/createprofile', {
          firstname: this.user.fname,
          lastname: this.user.lname,
          nickname: this.user.nickname,
          Gender: this.user.gender,
          Email: this.user.email,
          Birthday: this.user.birthday,
          Password1: getEncryptPassword(this.user.password1),
          Password2: getEncryptPassword(this.user.password2),
        })
        .then((response) => {
          console.log(response);
          router.push('Profile');
        }, (error) => {
          console.log(error)
        })
      this.init()
    }
  }
}
</script>
