<template>
    <div class="settingsContainer">
        <UserSettingsMenu />
        <div class="settingsContentContainer">
            <h1 id="editEmailPage">Edit Email Settings</h1>
            <hr>
            <form @submit.prevent class="editForm">
                <h3>Primary email</h3>
                <div class="primaryEmailContainer" justify="center" align="center">
                    <v-col cols="6">
                        <h4>{{ searchedUser.primary_email }}</h4>
                        <v-progress-linear
                            v-if="loadingEmails"
                            indeterminate
                            rounded
                            color="white"
                        />
                    </v-col>
                </div>
                <h3>Secondary emails:</h3>
                <div class="secondaryEmailContainer"
                       v-for="email in searchedUser.additional_email" v-bind:key="email">
                    <h4 v-on:click="openEmailEditBox(email)">{{email}}</h4>
                    <svg v-on:click="openEmailEditBox(email)" class="editIcon" xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24">
                        <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" />
                        <path d="M0 0h24v24H0z" fill="none" />
                    </svg>
                    <button class="genericButton" v-on:click="updatePrimaryEmail(email)">Set Primary</button>
                    <svg class="removeEmailButton" v-on:click="removeEmail(email)" xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24">
                        <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" />
                        <path d="M0 0h24v24H0z" fill="none" />
                    </svg>
                    <div class="floatClear"></div>
                </div>
                <v-row no-gutters class="secondaryEmailContainer"
                       justify="center" align="center"
                       v-if="loadingEmails">
                    <v-col cols="6">
                        <v-progress-linear
                            indeterminate
                            rounded
                        />
                    </v-col>
                </v-row>
                <h6 class="addExtraEmailErrorMessage" v-if="errorMsg">{{errorMsg}}</h6>
                <form class="addEmailForm" @submit.prevent v-if="searchedUser.additional_email.length < 4">
                    <input class="addEmailInput" v-if="showButton" v-model="textInput" type="email" placeholder="Enter new email (Up to 4)" required>
                    <button class="genericConfirmButton addEmailButton" v-if="showButton" v-on:click="addNewEmail(textInput)">Add</button>
                </form>
            </form>
        </div>
        <transition name="fade">
            <div class="transparentCover" v-if="showEditBox" @click="showEditBox = false"></div>
        </transition>
        <transition name="fadeup">
            <div class="editEmailContainer" v-if="showEditBox">
                <h1>Edit Email</h1>
                <h6 class="addExtraEmailErrorMessage" v-if="editErrorMsg">{{editErrorMsg}}</h6>
                <form class="addEmailForm" @submit.prevent style="margin-bottom:0;">
                    <input class="addEmailInput" v-model="editEmailInput" type="email" placeholder="Secondary Email" required>
                    <button class="genericConfirmButton updateEmailButton" v-on:click="editExistingEmail()">Save</button>
                </form>
            </div>
        </transition>
        <div class="floatClear"></div>
        <v-snackbar
            v-model="snackbar"
            :color="snackbarColour"
            top
        >
            {{ snackbarText }}
            <v-btn
                @click="snackbar = false"
                color="white"
                text
            >
                Close
            </v-btn>
        </v-snackbar>
    </div>
</template>

<script>
    import {mapState, mapActions, mapGetters} from 'vuex';
    import UserSettingsMenu from './ProfileSettingsMenu';
    const LIMIT_NUM_EMAIL = 4;
    const SUCCESS = "success";
    const ERROR = "error";

    export default {
        name: "EmailSettings",
        components: {
            UserSettingsMenu
        },
        data() {
            return {
                primary_email: "",
                additional_email: [],
                showButton: true,
                textInput: '',
                newEmail: '',
                limit_num_email: LIMIT_NUM_EMAIL,
                tempOldEmail: '',
                editEmailInput: '',
                searchedUser: {primary_email: "", additional_email: []},
                showEditBox: false,
                errorMsg: "",
                editErrorMsg: "",
                loadingEmails: true,
                snackbar: false,
                snackbarText: null,
                snackbarColour: '',
            }
        },
        computed: {
            ...mapState(['user']),
            ...mapGetters(['user']),
        },
        methods: {
            ...mapActions(['updateUserEmail',"getAllEmails", "getUserById", "addEmail", "editEmail"]),

            /*
               Adds a new email into the secondary emails lists. Prevents the user from entering empty text, adding more
               than 5 emails or from trying to enter an existing email.
            */
            async addNewEmail(textInput) {
                this.errorMsg = "";
                if (this.searchedUser.additional_email.length === LIMIT_NUM_EMAIL) {
                    this.showButton = false;
                    return;
                }
                const emails = await this.getAllEmails();
                if (textInput === "") {
                    this.showSnackbar("Please enter an email.", ERROR);
                    return;
                }
                if (this.searchedUser.additional_email.includes(textInput) || textInput == this.searchedUser.primary_email || emails.data.includes(textInput)) {
                    this.showSnackbar("Email already in use.", ERROR);
                } else if (textInput != "" && (/[^\s]+@[^\s]+/.test(textInput))) {
                    try {
                        await this.addEmail({'id': this.searchedUser.profile_id, 'newEmail':[this.textInput]});
                        this.searchedUser.additional_email.push(this.textInput);
                        this.updateUserEmail(this.searchedUser);
                        var tempThis = this;
                        this.showSnackbar("Email successfully added.", SUCCESS);
                        setTimeout(function() {
                            tempThis.textInput = "";
                        }, 10);
                    } catch (err) {
                        this.showSnackbar("Email already in use.", ERROR);
                        this.textInput = "";
                    }
                }
            },

            /*
             Function that updates the primary email of an user by picking one from the secondary emails list and adding the
             previous primary email to the secondary list.
             */
            updatePrimaryEmail(additional_email) {
                const index = this.searchedUser.additional_email.indexOf(additional_email);
                if (index > -1) {
                    this.searchedUser.additional_email.splice(index, 1);
                }
                this.searchedUser.additional_email.push(this.searchedUser.primary_email);
                this.searchedUser.primary_email = additional_email;
                this.updateUserEmail(this.searchedUser);
                this.editEmail({'id': this.searchedUser.profile_id, 'primaryEmail': this.searchedUser.primary_email, 'additionalEmail': this.searchedUser.additional_email});
                this.showSnackbar("Emails successfully switched.", SUCCESS);
            },

            /*
              Function that opens up a small edit box that allows the user to edit a secondary email.
            */
            openEmailEditBox(additional_email) {
                this.tempOldEmail = additional_email;
                this.editEmailInput = additional_email;
                this.showEditBox = true;
            },

            /*
              Function to edit a secondary email. Shows an error message if the entered email is already in use.
            */
            async editExistingEmail() {
                this.editErrorMsg = "";
                const emails = await this.getAllEmails();
                if (this.searchedUser.additional_email.includes(this.editEmailInput) || this.editEmailInput == this.searchedUser.primary_email || emails.data.includes(this.editEmailInput)) {
                    this.showSnackbar("Email already in use.", ERROR);
                } else if (/[^\s]+@[^\s]+/.test(this.editEmailInput)) {
                    const index = this.searchedUser.additional_email.indexOf(this.tempOldEmail);
                    this.searchedUser.additional_email[index] = this.editEmailInput;
                    this.showEditBox = false;
                    this.updateUserEmail(this.searchedUser);
                    this.editEmail({'id': this.searchedUser.profile_id, 'primaryEmail': this.searchedUser.primary_email, 'additionalEmail': this.searchedUser.additional_email});
                    this.editErrorMsg = "";
                    this.showSnackbar("Email successfully edited.", SUCCESS);
                }
            },

            /*
              Function that removes an email from the secondary emails list
            */
            removeEmail(email) {
                if (this.searchedUser.additional_email.length === 1) {
                    this.searchedUser.additional_email = [];
                } else {
                    const index = this.searchedUser.additional_email.indexOf(email);
                    if (index > -1) {
                        this.searchedUser.additional_email.splice(index, 1);
                    }
                }
                if (this.searchedUser.additional_email.length > 0) {
                    this.showButton = true;
                }
                this.updateUserEmail(this.searchedUser);
                this.editEmail({'id': this.searchedUser.profile_id, 'primaryEmail': this.searchedUser.primary_email, 'additionalEmail': this.searchedUser.additional_email});
                this.showSnackbar("Email removed.", SUCCESS);
            },

            /*
                Uses user id from url to request user data.
             */
            async loadSearchedUser() {
                if(this.$route.params.profileId == null || this.$route.params.profileId == ""){
                    this.$router.push('/settings/email/'+this.user.profile_id);
                    this.searchedUser = this.user;
                }else{
                    var tempUserData = await this.getUserById(this.$route.params.profileId);
                    if(tempUserData == "Invalid permissions"){
                        this.$router.push('/settings/email/'+this.user.profile_id);
                        this.searchedUser = this.user;
                    }else{
                        this.searchedUser = tempUserData;
                    }
                }
                this.loadingEmails = false;
            },

            /**
             * Shows a pop-up message to the user displaying the result of a function call.
             * @param text: the text to be displayed to the user.
             * @param colour: the colour to be displayed with the snackbar.
             */
            showSnackbar(text, colour) {
                this.snackbarText = text;
                this.snackbarColour = colour;
                this.snackbar = true;
            }
        },
        mounted() {
            if (!this.user.isLogin) {
                this.$router.push('/login');
            } else {
                this.loadSearchedUser();
            }
        }
    };
</script>
