<template>
    <div class="settingsContainer">
        <UserSettingsMenu/>
        <div class="settingsContentContainer">
            <h1>Change Password</h1>
            <hr>
            <div>
                <form @submit.prevent>
                    <input v-if="user.permission_level === 0" class="changePasswordInput" type="password" name="password" placeholder="Current Password" v-model="oldPassword">
                    <div class="errorMessageContainer">
                        <h6 class="errorMessage" id="password_incorrect" hidden="true">Incorrect password</h6>
                    </div>
                    <input class="changePasswordInput" type="password" name="newPassword" placeholder="New Password" v-model="newPassword">
                    <div class="errorMessageContainer">
                        <h6 class="errorMessage" id="passwords_dont_match" hidden="true">Passwords must be matching</h6>
                    </div>
                    <input class="changePasswordInput" type="password" name="confirmPassword" placeholder="Re-enter Password" v-model="confirmPassword">
                    <div class="errorMessageContainer">
                        <h6 class="errorMessage" id="other_error" hidden="true"/>
                        <h6 class="successMessage" id="success" hidden="true">Password successfully updated</h6>
                    </div>
                    <button class="genericConfirmButton updatePasswordButton" v-on:click="submitPasswordChange()" type="submit">Change Password</button>
                </form>
            </div>
        </div>
    </div>
</template>

<script>
    import UserSettingsMenu from '@/components/Settings/UserSettingsMenu';
    import {apiUser} from "../../api";
    import { mapGetters } from 'vuex'



    export default {
        components: {
            UserSettingsMenu
        },
        data() {
            return {
                oldPassword: '',
                newPassword: '',
                confirmPassword: '',
            }
        },
        computed: {
            ...mapGetters(['user']),
            /*
                Checks if the new password provided is a valid one.
             */
            validation() {
                return {
                    oldPassword: this.oldPassword !== '' || this.user.permission_level > 0,
                    match: this.newPassword == this.confirmPassword,
                    length: /.{8,}/.test(this.newPassword),
                    number: /\d/.test(this.newPassword),
                    uppercase: /[A-Z]/.test(this.newPassword),
                }
            }
        },
        methods: {
            /*
                Hides error messages once valid data is provided.
             */
            hideErrorMessages() {
                document.getElementById("password_incorrect").hidden = true;
                document.getElementById("passwords_dont_match").hidden = true;
                document.getElementById("other_error").hidden = true;
            },

            /*
                Uses the validation function to check that the provided data is valid. If everything is valid, sends a
                request to the server side to update the password. Provides the user with appropriate error messages if
                the password change was unsuccessful.
             */
            submitPasswordChange() {
                if (this.validation.oldPassword) {
                    if (this.validation.match) {
                        if (this.validation.length) {
                            if (this.validation.number) {
                                if (this.validation.uppercase) {
                                    apiUser.changePassword(this.user.profile_id, this.oldPassword, this.newPassword, this.confirmPassword).then((response) => {
                                        this.hideErrorMessages();
                                        document.getElementById("success").hidden = false;
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
                                            document.getElementById("other_error").innerText = responseData.Errors;
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
                    } else {
                        console.log("Yay!");
                        document.getElementById("other_error").hidden = false;
                        document.getElementById("other_error").innerText = "The repeated passwords do not match";
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
