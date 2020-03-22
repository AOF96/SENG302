<template>
    <div>
        <NavBar />
        <div class="credentials-box-wrap">
            <div id="credentials-box">
                <h1>Sign Up</h1>
                <h2>Create an account</h2>

                <form @submit.prevent>
                    <div class="signup-row">
                        <h6 class="signup_error" id="missing_field" hidden="true"></h6>
                    </div>
                    <div class="signup-row">
                        <input class="fmName" v-model="user.firstname" name="fname" type="text" placeholder="First Name*" required>
                        <input class="fmName" v-model="user.middlename" name="middlename" type="text" placeholder="Middle Name">
                    </div>

                    <div class="signup-row">
                        <input class="signupInput-lastname" v-model="user.lastname" name="lname" type="text" placeholder="Last Name*" required>
                    </div>
                    <div class="signup-row">
                        <input v-model="user.nickname" name="nickname" type="text" placeholder="Nickname">
                        <select v-model="user.gender" name="gender" placeholder="Gender"  value="Gender" required>
                            <option selected disabled hidden>Gender</option>
                            <option>Non-Binary</option>
                            <option>Female</option>
                            <option>Male</option>
                        </select>
                    </div>

                    <div class="signup-row">
                        <textarea v-model="user.bio" class="signupTextarea" name="bio" type="text" placeholder="Bio"></textarea>
                    </div>

                    <div class="signup-row">
                        <input v-model="user.primary_email" class="signupInput-email" name="email" type="email" placeholder="Email*" required>
                    </div>
                    <div class="signup-row">
                        <h3 id="signupText-birthday">Birthday</h3>
                        <input v-model="user.date_of_birth" class="signupInput-birthday" name="birthday" type="date" placeholder="Birthday" required>
                    </div>

                    <div class="signup-row">
                        <h3 id="fitnessLevelText">Fitness Level</h3>
                        <select id="levels" v-model="user.fitness" name="fitnesslevel" placeholder= "Fitness Level"  value="Fitness" required>
                            <option selected disabled hidden>Select your level</option>
                            <option>0</option>
                            <option>1</option>
                            <option>2</option>
                            <option>3</option>
                            <option>4</option>
                            <option>5</option>
                        </select>
                    </div>

                    <div class="signup-row">
                        <input v-model="password1" class="signupInput-password" name="pass1" type="password" placeholder="Password*" required>
                    </div>

                    <div class="signup-row">
                        <input v-model="password2" class="signupInput-password" name="pass2" type="password" placeholder="Password Again*" required>
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
    import router from "../router";
    import { mapState, mapActions } from 'vuex'
    import { apiUser, helperFunction } from '../api'

    import NavBar from '@/components/NavBar'

    const ERR_MSG_FNAME = 'Please enter your First name';
    const ERR_MSG_LNAME = 'Please enter your Last name';
    const ERR_MSG_GENDER = 'Please select your Gender';
    const ERR_MSG_EMAIL = 'Please enter a valid Email';
    const ERR_MSG_BIRTHDAY = 'Please select a valid date of birth';
    const ERR_MSG_PASS_MATCH = 'Password must match';
    const ERR_MSG_PASS_NUMBER = 'Password must include at least 1 number';
    const ERR_MSG_PASS_LENGTH = 'Password must be longer than 8 characters';
    const ERR_MSG_PASS_LOWERCASE = 'Password must include lowercase characters';
    const ERR_MSG_PASS_UPPERCASE = 'Password must include uppercases characters';
    const ERR_MSG_FITNESS = 'Please select your Fitness level';

    export default {
        name: 'Signup',
        components: {
            NavBar
        },
        data() {
            return {
                password1: '',
                password2: '',
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
                        uppercase: ERR_MSG_PASS_UPPERCASE,
                    },
                },
                failed: false
            }
        },

        computed: {
            ...mapState(['user']),

            validation() {
                return {
                    firstname: this.user.firstname !== '',
                    lastname: this.user.lastname !== '',
                    gender: this.user.gender !== 'Gender',
                    email: /[^\s]+@[^\s]+/.test(this.user.primary_email),
                    birthday: this.user.date_of_birth !== '' && Date.parse(this.user.date_of_birth) < new Date(),
                    fitnesslevel: this.user.fitnessLevel !== 'FitnessLevel',
                    password: {
                        match: this.password1 === this.password2,
                        length: /.{8,}/.test(this.password1),
                        number: /\d/.test(this.password1),
                        lowercase: /[a-z]/.test(this.password1),
                        uppercase: /[A-Z]/.test(this.password1),
                    },
                }
            },

            all_err_msg() {
                const validation = this.validation;
                const fields = Object.keys(validation);

                let err_msg = "";
                for (let i in fields) {
                    const field = fields[i];
                    if (!validation[field]) {
                        err_msg += '\n';
                        err_msg += this.err_msg[field]
                    }
                    const keys = Object.keys(validation[field]);
                    for (let i in keys) {
                        const key = keys[i];
                        if (!validation[field][key]) {
                            err_msg += '\n';
                            err_msg += this.err_msg[field][key];
                        }
                    }
                }
                return err_msg;
            },

            valid() {
                const valid = (this.all_err_msg === "");
                return valid;
            }
        },

        methods: {
            ...mapActions(['createUserProfile']),

            submitSignUp() {
                if (!this.valid) {
                    document.getElementById("missing_field").hidden = false;
                    document.getElementById("missing_field").innerText = this.all_err_msg;
                    this.failed = true;
                    return;
                } else {
                    document.getElementById("missing_field").hidden = true;
                    document.getElementById("missing_field").innerText = "";
                }

                apiUser.signUp(this.user.firstname, this.user.lastname, this.user.middlename, this.user.nickname, this.user.primary_email,
                    this.password1, this.user.bio, this.user.date_of_birth, this.user.gender, this.user.fitness).then((response) => {
                    console.log(response.data);
                    this.createUserProfile(response.data[0]);
                    helperFunction.addCookie("s_id", response.data[1]["sessionToken"], 365);
                    router.push('Profile');
                }, (error) => {
                    document.getElementById("missing_field").hidden = false;
                    document.getElementById("missing_field").innerText = error.response.data.Errors;
                    console.log(error)
                });
            }
        }
    }
</script>
