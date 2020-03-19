<template>
    <div id="settingsWrap">
        <UserSettingsMenu/>
        <div class="settingsContent">
            <h1>Change Password</h1>
            <hr>
            <div>
                <form @submit.prevent>
                    <input class="changePasswordFeild" type="password" name="password" placeholder="Current Password" v-model="password">
                    <div class="signup-row">
                        <h6 class="passwordChange_error" id="password_incorrect" hidden="true">Incorrect password</h6>
                    </div>
                    <input class="changePasswordFeild" type="password" name="newPassword" placeholder="New Password" v-model="newPassword">
                    <div class="signup-row">
                        <h6 class="passwordChange_error" id="passwords_dont_match" hidden="true">Passwords must be matching</h6>
                    </div>
                    <input class="changePasswordFeild" type="password" name="confirmPassword" placeholder="Re-enter Password" v-model="confirmPassword">
                    <div class="signup-row">
                        <h6 class="passwordChange_error" id="other_error" hidden="true"/>
                        <h6 class="passwordChange_success" id="success" hidden="true"/>
                    </div>
                    <button id="settingsPasswordSubmit" v-on:click="submitPasswordChange()" type="submit">Change Password</button>
                </form>
            </div>
        </div>
    </div>
</template>

<script>
    import UserSettingsMenu from '@/components/Settings/UserSettingsMenu';
    import {userInfo} from "../../globals";
    import {apiUser} from "../../api";
    import { mapGetters } from 'vuex'



    export default {
        components: {
            UserSettingsMenu
        },
        data() {
            return {
                profileId: userInfo.profileId,
                password: '',
                newPassword: '',
                confirmPassword: '',
            }
        },
        computed: {
            ...mapGetters(['user']),
            validation() {
                return {
                    oldPassword: this.password !== '',
                    match: this.newPassword == this.confirmPassword,
                    length: /.{8,}/.test(this.newPassword),
                    number: /\d/.test(this.newPassword),
                    uppercase: /[A-Z]/.test(this.newPassword),
                }
            }
        },
        methods: {
            hideErrorMessages() {
                document.getElementById("password_incorrect").hidden = true;
                document.getElementById("passwords_dont_match").hidden = true;
                document.getElementById("other_error").hidden = true;
            },

            submitPasswordChange() {
                if (this.validation.oldPassword) {
                    if (this.validation.match) {
                        if (this.validation.length) {
                            if (this.validation.number) {
                                if (this.validation.uppercase) {
                                    apiUser.changePassword(this.profileId, this.oldPassword, this.newPassword, this.confirmPassword).then((response) => {
                                        this.hideErrorMessages();
                                        document.getElementById("success").hidden = false;
                                        document.getElementById("other_error").innerText = "Password successfully updated";
                                        console.log(response);
                                    }, (error) => {
                                        const responseData = error.response.data;
                                        const responseCode = error.response.status;
                                        console.log(responseCode + ": " + responseData);

                                        if (responseCode === 400 && responseData === "oldPassword is incorrect") {
                                            document.getElementById("password_incorrect").hidden = false;
                                            document.getElementById("passwords_dont_match").hidden = true;
                                        } else if (responseCode === 400 && responseData === "newPassword and repeatPassword do no match") {
                                            document.getElementById("password_incorrect").hidden = true;
                                            document.getElementById("passwords_dont_match").hidden = false;
                                        } else {
                                            document.getElementById("password_incorrect").hidden = true;
                                            document.getElementById("passwords_dont_match").hidden = true;
                                            document.getElementById("other_error").hidden = false;
                                            document.getElementById("other_error").innerText = responseData;
                                        }
                                    });
                                } else {
                                    this.hideErrorMessages();
                                    document.getElementById("other_error").hidden = false;
                                    document.getElementById("other_error").innerText = "Must contain at least one uppercase character";
                                }
                            } else {
                                this.hideErrorMessages();
                                document.getElementById("other_error").hidden = false;
                                document.getElementById("other_error").innerText = "Must contain at least one number.";
                            }
                        } else {
                            this.hideErrorMessages();
                            document.getElementById("other_error").hidden = false;
                            document.getElementById("other_error").innerText = "Password length must at least be 8";
                        }
                    }
                } else {
                    this.hideErrorMessages();
                    document.getElementById("other_error").hidden = false;
                    document.getElementById("other_error").innerText = "Enter your old password";
                }
            }
        }
    }
</script>
