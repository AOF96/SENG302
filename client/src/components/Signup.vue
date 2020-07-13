<template>
  <div>
    <NavBar />
    <div class="signUpContainer">
      <div class="signUpFormContainer">
        <h1>Sign Up</h1>
        <h2>Create an account</h2>

        <form @submit.prevent>
          <h6 v-if="submissionError">{{ submissionError }}</h6>
          <div class="signUpRow">
            <input id="signup-firstname" class="signUpHalfWidthInput" v-model="user.firstname" name="fname" type="text" placeholder="First Name*" required/>
            <input id="signup-middlename" class="signUpHalfWidthInput" v-model="user.middlename" name="middlename" type="text" placeholder="Middle Name"/>
          </div>
          <p id="signup-firstname-err" v-if="!validation.firstname" class="errorMessage">{{ this.err_msg.firstname }}</p>
          <div class="signUpRow">
            <input id="signup-lastname" class="signUpInput" v-model="user.lastname" name="lname" type="text" placeholder="Last Name*" required/>
          </div>
          <p id="signup-lastname-err" v-if="!validation.lastname" class="errorMessage">{{ this.err_msg.lastname }}</p>
          <div class="signUpRow">
            <input id="signup-nickname" v-model="user.nickname" name="nickname" type="text" placeholder="Nickname" />
            <select id="signup-gender" v-model="user.gender" name="gender" required @change="genderChange($event)">
              <option selected disabled hidden value="Gender">Gender</option>
              <option>Non-Binary</option>
              <option>Female</option>
              <option>Male</option>
            </select>
          </div>
          <p id="genderErr" v-if="!validation.gender" class="errorMessage">{{ this.err_msg.gender }}</p>
          <p id="signup-bio-err" v-if="warning.bio" class="errorMessage">{{ this.bio_warning_msg }}</p>
          <div class="signUpRow">
            <textarea id="signup-bio" maxlength="255" v-model="user.bio" class="signupTextarea" name="bio" type="text" placeholder="Bio"></textarea>
          </div>
          <div class="signUpRow">
            <input id="signup-primary-email" v-model="user.primary_email" class="signUpInput" name="email" type="email" placeholder="Email*" required/>
          </div>
          <p id="signup-primary-email-err" v-if="user.primary_email && !validation.email" class="errorMessage">{{ this.err_msg.email }}</p>
          <div class="signUpRow">
            <h3 class="signUpText">Birthday</h3>
            <input id="signup-dob" v-model="user.date_of_birth" class="signUpInputBirthday" name="birthday" type="date" placeholder="Birthday" required/>
          </div>
          <p id="signup-dob-err" v-if="!validation.birthday" class="errorMessage">{{ this.err_msg.birthday }}</p>
          <div class="signUpRow">
            <h3 class="signUpText">Fitness Level</h3>
            <select id="signup-fitness-level" class="fitnessLevelSelect" v-model="user.fitness" name="fitnesslevel" placeholder="Fitness Level" value="Fitness" required>
                <option value="-1" selected disabled hidden>Fitness</option>
              <option value="0">I never exercise</option>
              <option value="1">I can walk a short distance</option>
              <option value="2">I can jog a short distance</option>
              <option value="3">I can run a medium distance</option>
              <option value="4">I can run a marathon</option>
            </select>
          </div>
          <div class="signUpRow">
            <input id="signup-password-1" v-model="password1" class="signUpInput" name="pass1" type="password" placeholder="Password*" required/>
          </div>
          <div class="signUpRow">
            <input id="signup-password-2" v-model="password2" class="signUpInput" name="pass2" type="password" placeholder="Password Again*" required/>
          </div>
          <ul id="signup-password-err" class="errorMessage">
            <li v-if="passwordInputIsFilled && !validation.password.match">{{ this.err_msg.password.match }}</li>
            <li v-if="passwordInputIsFilled && !validation.password.length">{{ this.err_msg.password.length }}</li>
            <li v-if="passwordInputIsFilled && !validation.password.number">{{ this.err_msg.password.number }}</li>
            <li v-if="passwordInputIsFilled && !validation.password.lowercase">{{ this.err_msg.password.lowercase }}</li>
            <li v-if="passwordInputIsFilled && !validation.password.uppercase">{{ this.err_msg.password.uppercase }}</li>
          </ul>
          <hr />
          <div class="signUpRow">
            <button v-on:click="submitSignUp()" class="loginButton" type="submit">Sign Up</button>
          </div>
        </form>
      </div>
      <h4>
        Already have an account?
        <router-link to="/Login">Login</router-link>
      </h4>
    </div>
  </div>
</template>

<script>
import {mapActions, mapGetters} from "vuex";

import NavBar from "./modules/NavBar";

const ERR_MSG_FNAME = "Please enter your First name";
const ERR_MSG_LNAME = "Please enter your Last name";
const ERR_MSG_GENDER = "Please select your Gender";
const ERR_MSG_EMAIL = "Please enter a valid Email";
const ERR_MSG_BIRTHDAY = "Please select a valid date of birth";
const ERR_MSG_PASS_MATCH = "Password must match";
const ERR_MSG_PASS_NUMBER = "Password must include at least 1 number";
const ERR_MSG_PASS_LENGTH = "Password must be longer than 8 characters";
const ERR_MSG_PASS_LOWERCASE = "Password must include lowercase characters";
const ERR_MSG_PASS_UPPERCASE = "Password must include uppercases characters";
const ERR_MSG_FITNESS = "Please select your Fitness level";
const WARNING_MSG_BIO = "You have reached the maximum amount of characters";

export default {
  name: "Signup",
  components: {
    NavBar
  },
  data() {
    return {
      firstOccurrence: true,
      errorMessages: [],
      submissionError: "",
      password1: "",
      password2: "",
      bio_warning_msg: WARNING_MSG_BIO,
      err_msg: {
        firstname: ERR_MSG_FNAME,
        lastname: ERR_MSG_LNAME,
        gender: ERR_MSG_GENDER,
        email: ERR_MSG_EMAIL,
        birthday: ERR_MSG_BIRTHDAY,
        fitnesslevel: ERR_MSG_FITNESS,
        password: {
          match: ERR_MSG_PASS_MATCH,
          number: ERR_MSG_PASS_NUMBER,
          length: ERR_MSG_PASS_LENGTH,
          lowercase: ERR_MSG_PASS_LOWERCASE,
          uppercase: ERR_MSG_PASS_UPPERCASE
        }
      },
    };
  },

  computed: {
    ...mapGetters(["user"]),

    warning() {
        return {
          bio: this.user.bio && this.user.bio.length >= 255
        };
    },

    /*
      Function that checks if the provided data is valid when signing up.
    */
    validation() {
      return {
        firstname: this.user.firstname !== "",
        lastname: this.user.lastname !== "",
        //Don't display the gender error message if the user has not done anything yet
        gender: this.user.gender !== "Gender" || this.firstOccurrence,
        email: /[^\s]+@[^\s]+/.test(this.user.primary_email),
        birthday: this.user.date_of_birth !== "" && this.birthday_validation,
        fitnesslevel: this.user.fitnessLevel !== -1,
        password: {
          match: this.password1 === this.password2,
          length: /.{8,}/.test(this.password1),
          number: /\d/.test(this.password1),
          lowercase: /[a-z]/.test(this.password1),
          uppercase: /[A-Z]/.test(this.password1)
        }
      };
    },
    /*
        The function validates the date if birth entered by the user and only allow when the user's age is 13
        or over but also less than 140.
    */
    birthday_validation() {
      var user_input_date = new Date(this.user.date_of_birth);
      if (user_input_date > Date.now()) {
        return false;
      }
      var diff_ms = Date.now() - user_input_date.getTime();
      var age_dt = new Date(diff_ms);
      var user_age = Math.abs(age_dt.getUTCFullYear() - 1970);
      return user_age >= 13 && user_age <= 140;
    },

    /*
       Returns true if all the provided data is valid.
    */
    valid() {
      this.all_err_msg(); // Get all the errors on the field and show it on the top of the page
      return this.errorMessages.length === 0;
    },

    passwordInputIsFilled() {
      return this.password1 || this.password2
    }
  },

  methods: {
    ...mapActions(["createUserProfile", "signUp"]),


    /*
       Returns an appropriate error message if something goes wrong when signing up.
       Sets the errorMessages variable that is printed on the top of the page
    */
    all_err_msg() {
      const validation = this.validation;
      const fields = Object.keys(validation);

      this.errorMessages = [];
      for (let i in fields) {
        const field = fields[i];
        if (!validation[field]) {
          this.errorMessages.push(this.err_msg[field]);
        }
        const keys = Object.keys(validation[field]);
        for (let i in keys) {
          const key = keys[i];
          if (!validation[field][key]) {
            this.errorMessages.push(this.err_msg[field][key]);
          }
        }
      }

    },

    /**
     * Toggles firstOccurrence if the gender has been changed or the user has clicked 'Sign Up' to ensure a gender has
     * been selected
     */
    genderChange() {
      this.firstOccurrence = false;
    },

    /*
      Submits a request to register a new user. Checks if there are missing fields when signing up.
    */
    submitSignUp() {
      this.firstOccurrence = false;
      this.submissionError = "";
      if (!this.valid) {
        return;
      }
      console.log(this.user);
      this.signUp(
          {'firstName': this.user.firstname,
        'lastName': this.user.lastname,
        'middleName': this.user.middlename,
        'nickName': this.user.nickname,
        'email': this.user.primary_email,
        'password': this.password1,
        'bio': this.user.bio,
        'dateOfBirth': this.user.date_of_birth,
        'gender': this.user.gender,
        'fitnessLevel': Number(this.user.fitness)}
      ).then(
        response => {
          this.createUserProfile(response.data);
          this.$router.push('profile?u='+response.data.profile_id);
        },
        error => {
          this.submissionError = error.response.data.Errors;
          console.log(error);
        }
      );
    }
  }
};
</script>

<style scoped>
  @import '../../public/styles/pages/signUpStyle.css';
</style>
