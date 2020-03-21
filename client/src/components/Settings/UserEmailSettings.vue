<template>
<div id="settingsWrap">
    <UserSettingsMenu />
    <div class="settingsContent">
        <h1>Edit Email Settings</h1>
        <hr>
        <h3>Primary email</h3>
        <div class="emailBlock">
            <h4>{{ user.primary_email }}</h4>
        </div>
        <h3>Secondary emails:</h3>
        <div class="emailBlock emailSecondary" v-for="email in user.additional_email" v-bind:key="email">
            <h4 v-on:click="openEmailEditBox(email)">{{email}}</h4>
            <svg v-on:click="openEmailEditBox(email)" class="editIcon" xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24">
                <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" />
                <path d="M0 0h24v24H0z" fill="none" /></svg>
            <button class="setPrimaryButton" v-on:click="updatePrimaryEmail(email)">Set Primary</button>
            <svg class="removeEmailButton" v-on:click="removeEmail(email)" xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24">
                <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" />
                <path d="M0 0h24v24H0z" fill="none" /></svg>
        </div>
        <!-- <div v-if="additional_email.length < 4"> -->
        <form @submit.prevent>
            <input id="addEmailInput" v-if="showButton" v-model="textInput" type="email" placeholder="Enter new email (Up to 4)" required>
            <button id="addEmailButton" v-if="showButton" v-on:click="addEmail(textInput)">Add</button>
        </form>
        <!-- </div> -->
    </div>
    <transition name="fade">
        <div id="cover" v-if="showEditBox" @click="showEditBox = false"></div>
    </transition>
    <transition name="fadeup">
        <div class="floatingBox" id="editEmailBox" v-if="showEditBox">
            <h1>Edit Email</h1>
            <form @submit.prevent>
                <input id="editEmailInput" v-model="editEmailInput" type="email" placeholder="Secondary Email" required>
                <button id="editEmailButton" v-on:click="editEmail()">Update</button>
            </form>
        </div>
    </transition>
</div>
</template>

<script>
import {mapState, mapActions, mapGetters} from 'vuex';
import UserSettingsMenu from '@/components/Settings/UserSettingsMenu';
import {apiUser} from "../../api";
const LIMIT_NUM_EMAIL = 4;

export default {
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
            showEditBox: false,
            emails: apiUser.getAllEmails()
        }
    },
    computed: {
        ...mapState(['user']),
        ...mapGetters(['user'])
    },
    methods: {
        ...mapActions(['updateUserEmail']),

        /*
           Adds a new email into the secondary emails lists. Prevents the user from entering empty text, adding more
           than 5 emails or from trying to enter an existing email.
        */
        async addEmail(textInput) {
            if (this.user.additional_email.length === 4) {
                alert("You already have 5 emails, delete one if you want to add more.");
                this.showButton = false;
                return;
            }
            const emails = await apiUser.getAllEmails();
            if (this.user.additional_email.includes(textInput) || textInput == this.user.primary_email || emails.data.includes(textInput)) {
                alert("Email already in use.");
            } else if (textInput != "" && (/[^\s]+@[^\s]+/.test(textInput))) {
                this.user.additional_email.push(this.textInput);
                this.updateUserEmail(this.user);
                var tempThis = this;
                setTimeout(function() {
                    tempThis.textInput = "";
                }, 10);
                await apiUser.addEmails(this.user.profile_id, [this.textInput]);
            }
        },

        /*
         Function that updates the primary email of an user by picking one from the secondary emails list and adding the
         previous primary email to the secondary list.
         */
        updatePrimaryEmail(additional_email) {
            const index = this.user.additional_email.indexOf(additional_email);
            if (index > -1) {
                this.user.additional_email.splice(index, 1);
            }
            this.user.additional_email.push(this.user.primary_email);
            this.user.primary_email = additional_email;
            console.log(this.user.primary_email, this.user.additional_email);
            this.updateUserEmail(this.user);
            apiUser.editEmail(this.user.profile_id, this.user.primary_email, this.user.additional_email);
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
          Function to edit a secondary email.
        */
        async editEmail() {
            const emails = await apiUser.getAllEmails();
            if (this.user.additional_email.includes(this.editEmailInput) || this.editEmailInput == this.user.primary_email || emails.data.includes(this.editEmailInput)) {
                alert("Email already in use.");
            } else if (/[^\s]+@[^\s]+/.test(this.editEmailInput)) {
                const index = this.user.additional_email.indexOf(this.tempOldEmail);
                this.user.additional_email[index] = this.editEmailInput;
                this.showEditBox = false;
                this.updateUserEmail(this.user);
                apiUser.editEmail(this.user.profile_id, this.user.primary_email, this.user.additional_email);
            }
        },

        /*
          Function that removes an email from the secondary emails list
        */
        removeEmail(email) {
            if (this.user.additional_email.length === 1) {
                this.user.additional_email = [];
            } else {
                const index = this.user.additional_email.indexOf(email);
                if (index > -1) {
                    this.user.additional_email.splice(index, 1);
                }
            }
            if (this.user.additional_email.length > 0) {
                this.showButton = true;
            }
            this.updateUserEmail(this.user);
            apiUser.editEmail(this.user.profile_id, this.user.primary_email, this.user.additional_email);
        }
    }
}
</script>
