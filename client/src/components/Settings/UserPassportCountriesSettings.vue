<template>
    <div id="settingsWrap">
        <UserSettingsMenu />
        <div class="settingsContent">
            <h1>Edit Passport Countries</h1>
            <hr>
            <div class="countryBox" v-for="country in searchedUser.passports" v-bind:key="country">
                <h4 class="countryDisplay">{{country}}</h4>
                <button class="removeCountryButton" v-on:click="removePassportCountries(country)">remove</button>
                <div class="floatClear"></div>
            </div>
            <div id="countryActions">
                <form @submit.prevent>
                    <select v-model="adding_country"
                            name="passportCountries"
                            placeholder="Passport Countries"
                            value="Passport Countries"
                            id="passportCountriesInput"
                            required>
                        <option selected disabled hidden>Passport Countries</option>
                        <option v-for="addingCountry in countries_option" v-bind:key="addingCountry">
                            {{addingCountry}}
                        </option>
                    </select>
                    <button id = "addPassportButton" v-on:click="addPassportCountries()">Add</button>
                    <button id ="saveChangesButton" v-on:click="savePassportCountries()">Save</button>
                    <div class="floatClear"></div>
                </form>
            </div>
            <h6 class="edit_success" id="passport_success" hidden="false">Saved successfully</h6>
            <h6 class="edit_error" id="passport_error" hidden="false">An error has occurred</h6>
        </div>
    </div>
</template>

<script>
    import UserSettingsMenu from '@/components/Settings/UserSettingsMenu'


    import axios from "axios";
    const COUNTRIES_URL = 'https://restcountries.eu/rest/v2/all'
    import {apiUser} from "../../api";
    import {mapState, mapActions, mapGetters} from 'vuex';

    export default {
        components: {
            UserSettingsMenu
        },
        data() {
            return {
                countries_option: [],
                adding_country: "Passport Countries",
                num_of_countries: 1,
                searchedUser: {}
            }
        },
        computed: {
            ...mapState(['user']),
            ...mapGetters(['user'])
        },
        mounted() {
            this.loadSearchedUser();
        },
        methods: {
            ...mapActions(['updatePassports']),
            /*
              Displays all the possible options a user can pick when selecting a new passport country. Prevents the user
              from selecting the same country twice.
            */
            startUp() {
                this.searchedUser.passports = this.searchedUser.passports.slice();
                axios.get(COUNTRIES_URL)
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
                if(this.$route.query.u == null){
                    this.$router.push('/settings/passport_countries?u='+this.user.profile_id);
                    this.searchedUser = this.user;
                }else{
                    var tempUserData = await apiUser.getUserById(this.$route.query.u);
                    if(tempUserData == "Invalid permissions"){
                        this.$router.push('/settings/passport_countries?u='+this.user.profile_id);
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
                if(!this.adding_country || this.adding_country == "Passport Countries") return
                this.searchedUser.passports.push(this.adding_country)
                const index = this.countries_option.indexOf(this.adding_country)
                if (index == -1) return
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
                console.log(this.countries_code_name_option);
                apiUser.editProfile(this.searchedUser.profile_id, this.searchedUser.firstname, this.searchedUser.lastname, this.searchedUser.middlename,
                    this.searchedUser.nickname, this.searchedUser.primary_email, this.searchedUser.bio, this.searchedUser.date_of_birth, this.searchedUser.gender,
                    Number(this.searchedUser.fitness), this.searchedUser.additional_email, this.searchedUser.passports).then((response) => {
                    document.getElementById("passport_success").hidden = false;
                    document.getElementById("passport_error").hidden = true;
                    console.log(response);
                }, (error) => {
                    document.getElementById("passport_error").hidden = false;
                    document.getElementById("passport_error").innerText = error.response.data.Errors;
                    document.getElementById("passport_success").hidden = true;
                    console.log(error);
                });
            }
        }
    }
</script>
