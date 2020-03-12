<template>
    <div>
        <NavBar/>
        <div id="loginBox" class="credentials-box-wrap">
            <div id="credentials-box">
                <h1>Login</h1>
                <h2>Sign in to your account</h2>
                <form @submit.prevent>
                    <div class="signup-row">
                        <input type="email" v-model="email" class="loginInput-email" name="email" placeholder="Email"
                               required>
                    </div>
                    <div class="signup-row">
                        <input type="password" v-model="password" class="loginInput-password" name="password"
                               placeholder="Password" required>
                    </div>
                    <hr>
                    <input type="submit" v-on:click="submitLogin()" id="signupButton-submit" value="Login">
                </form>
            </div>
            <h4>Don't have an account?
                <router-link to="/Signup">Sign Up</router-link>
            </h4>
        </div>
    </div>
</template>

<script>

    import axios from 'axios'
    import router from "../router";
    import {getEncryptPassword} from "../common.js"

    import NavBar from '@/components/NavBar'
    import {userInfo} from '../globals';

    const SERVER_URL = 'https://4967d4f4-8301-42d1-a778-e3d150633644.mock.pstmn.io'

    export default {
        name: 'Login',
        components: {
            NavBar
        },
        data() {
            return {
                email: "",
                password: ""
            }
        },

        methods: {
            submitLogin() {
                if (this.email.trim(), this.password.trim()) {
                    axios.post(SERVER_URL + '/login', {
                        email: this.email,
                        password: getEncryptPassword(this.password),
                    })
                        .then((response) => {
                            var users = response.data;
                            var registeredUser = false;

                            if  (/[^\s]+@[^\s]+/.test(this.email)) {

                                for (var i = 0; i < users.length; i++) {
                                    var currentUser = users[i];

                                    // Checking if the user exists
                                    if (this.email == currentUser.email) {
                                        registeredUser = true;
                                        if (getEncryptPassword(this.password) == currentUser.password) {
                                            userInfo.firstname = currentUser.firstname,
                                                userInfo.lastname = currentUser.lastname,
                                                userInfo.middlename = currentUser.middlename,
                                                userInfo.nickname = currentUser.nickname,
                                                userInfo.gender = currentUser.gender,
                                                userInfo.email = currentUser.email,
                                                userInfo.birthday = currentUser.birthday,
                                                userInfo.isLogin = true,
                                                userInfo.fitness = currentUser.fitnesslevel,
                                                userInfo.profile_id = currentUser.profile_id

                                            console.log(response.data.msg1);
                                            router.push('Profile');
                                        } else {
                                            alert("Invalid password or email");
                                        }
                                    }
                                }
                                if (!registeredUser) {
                                    alert('Email entered is not registered');
                                }
                            }
                        }, (error) => {
                            console.log(error);
                        })
                } else {
                    alert("Please fill all required fields");
                }
            }
        }
    }


</script>
