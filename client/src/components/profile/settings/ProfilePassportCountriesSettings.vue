<template>
    <div class="settingsContainer">
        <UserSettingsMenu />
        <div class="settingsContentContainer">
            <h1>Edit Passport Countries</h1>
            <hr>
            <div class="itemContainer" v-for="country in searchedUser.passports" v-bind:key="country">
                <h4>{{country}}</h4>
                <button class="genericDeleteButton" v-on:click="removePassportCountries(country)">remove</button>
                <div class="floatClear"></div>
            </div>
            <div>
                <form class="formTopSpacing" @submit.prevent>
                    <select v-model="adding_country"
                            name="passportCountries"
                            placeholder="Passport Countries"
                            value="Passport Countries"
                            class="itemSelect"
                            required>
                        <option selected disabled hidden>Passport Countries</option>
                        <option v-for="addingCountry in countries_option" v-bind:key="addingCountry">
                            {{addingCountry}}
                        </option>
                    </select>
                    <button class="genericConfirmButton addItemButton" v-on:click="addPassportCountries()">Add</button>
                    <button class="genericConfirmButton saveButton" v-on:click="savePassportCountries()">Save All Passport Countries</button>
                    <div class="floatClear"></div>
                </form>
            </div>
        </div>
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
    import UserSettingsMenu from './ProfileSettingsMenu';
    const COUNTRIES_URL = 'https://restcountries.eu/rest/v2/all'
    import { mapState, mapActions, mapGetters } from 'vuex'

    export default {
        components: {
            UserSettingsMenu
        },
        data() {
            return {
                countries_option: [],
                adding_country: "Passport Countries",
                num_of_countries: 1,
                searchedUser: {},
                snackbar: false,
                snackbarText: null,
                snackbarColour: '',
            }
        },
        computed: {
            ...mapState(['user']),
            ...mapGetters(['user'])
        },
        mounted() {
            if (!this.user.isLogin) {
                this.$router.push('/login');
            } else {
                this.loadSearchedUser();
            }
        },
        methods: {
            ...mapActions(['updatePassports', "getDataFromUrl", "getUserById","editProfile"]),
            /*
              Displays all the possible options a user can pick when selecting a new passport country. Prevents the user
              from selecting the same country twice.
            */
            startUp() {
                this.searchedUser.passports = this.searchedUser.passports.slice();
                this.getDataFromUrl(COUNTRIES_URL)
                    .then((response) => {
                        const countries = []
                        const data = response.data
                        for (let country in data) {
                            let country_name = data[country].name
                            countries.push(country_name)
                        }
                        for(let country of this.searchedUser.passports) {
                            const index = countries.indexOf(country)
                            if (index === -1) continue
                            countries.splice(index, 1)
                        }
                        this.countries_option = countries
                    })
                    .catch(error => console.log(error));
            },

            /*
                Uses user id from url to request user data.
             */
            async loadSearchedUser() {
                if(this.$route.params.profileId == null || this.$route.params.profileId === ""){
                    this.$router.push('/settings/passport_countries/'+this.user.profile_id);
                    this.searchedUser = this.user;
                }else{
                    let tempUserData = await this.getUserById(this.$route.params.profileId);
                    if(tempUserData === "Invalid permissions"){
                        this.$router.push('/settings/passport_countries/'+this.user.profile_id);
                        this.searchedUser = this.user;
                    }else{
                        this.searchedUser = tempUserData;
                    }
                }
                this.startUp();
            },

            /*
              Removes a passport country from an user account.
            */
            removePassportCountries(country) {
                const index = this.searchedUser.passports.indexOf(country)
                if (index === -1) return
                this.searchedUser.passports.splice(index, 1)
                this.countries_option.push(country)
                this.countries_option.sort()
                this.updatePassports(this.searchedUser)
            },

            /*
                Adds a new passport country to an user account.
             */
            addPassportCountries() {
                if(!this.adding_country || this.adding_country === "Passport Countries") return
                this.searchedUser.passports.push(this.adding_country)
                const index = this.countries_option.indexOf(this.adding_country)
                if (index === -1) return
                this.countries_option.splice(index, 1)
                this.adding_country = "Passport Countries"
                this.updatePassports(this.searchedUser)
            },

            /*
                Makes a request to the server side in order to add a passport country into the database. Provides
                appropriate error messages if the update was unsuccessful.
             */
            savePassportCountries() {
                this.updatePassports(this.searchedUser);
                this.editProfile(this.searchedUser).then((response) => {
                    this.showSnackbar("Passport saved successfully.", "success");
                    console.log(response);
                }, (error) => {
                    this.showSnackbar(error.response.data.Errors, "error");
                });
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
        }
    }
</script>
