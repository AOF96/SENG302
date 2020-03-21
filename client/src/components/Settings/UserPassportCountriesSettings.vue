<template>
<div id="settingsWrap">
    <UserSettingsMenu />
    <div class="settingsContent">
        <h1>Edit Passport Countries</h1>
        <hr>
        <br>
        <div class="signup-row">
            <h6 class="edit_success" id="passport_success" hidden="false">Saved successfully</h6>
            <h6 class="edit_error" id="passport_error" hidden="false">An error has occurred</h6>
        </div>
        <div class="countryBox" v-for="country in user.user.passports" v-bind:key="country">
            <h4 class="countryDisplay">{{country}}</h4>
            <button class="removeCountryButton" v-on:click="removePassportCountries(country)">remove</button>
            <div class="floatClear"></div>
        </div>

        <div id="countryActions">
          <form @submit.prevent>
            <select
                v-model="adding_country"
                name="passportCountries"
                placeholder="Passport Countries"
                value="Passport Countries"
                required
            >
                <option selected disabled hidden>Passport Countries</option>
                <option v-for="addingCountry in countries_option" v-bind:key="addingCountry">
                    {{addingCountry}}
                </option>
            </select>
            <button v-on:click="addPassportCountries()">Add passport countries</button>
              <button v-on:click="savePassportCountries()">Save Changes</button>
          </form>
        </div>
    </div>
</div>
</template>

<script>
import UserSettingsMenu from '@/components/Settings/UserSettingsMenu'


import axios from "axios";
const COUNTRIES_URL = 'https://restcountries.eu/rest/v2/all?fields=name'
import {apiUser} from "../../api";
import { mapState, mapActions } from 'vuex'

export default {
    components: {
        UserSettingsMenu
    },
    data() {
        return {
            countries_option: [],
            adding_country: "",
            num_of_countries: 1,
        }
    },
    computed: {
        ...mapState(['user']),
    },
    created: function() {
        axios.get(COUNTRIES_URL)
            .then((response) => {
                const countries = []
                const data = response.data
                for (let country in data) {
                    let country_name = data[country].name
                    countries.push(country_name)
                }

                for(let country of this.user.user.passports) {
                    const index = countries.indexOf(country)
                    if (index === -1) continue
                    countries.splice(index, 1)
                }
                this.countries_option = countries
            })
            .catch(error => console.log(error))
    },
    mounted() {
        this.startUp()
    },
    methods: {
        ...mapActions(['updatePassports']),
         startUp() {
            console.log('init')
            this.user.user.passports = this.user.user.passports.slice()
        },
        removePassportCountries(country) {
            const index = this.user.user.passports.indexOf(country)
            if (index === -1) return
            this.user.user.passports.splice(index, 1)
            this.countries_option.push(country)
            this.countries_option.sort()
            this.updatePassports(this.user.user)
        },
        addPassportCountries() {
            if(!this.adding_country) return
            this.user.user.passports.push(this.adding_country)
            const index = this.countries_option.indexOf(this.adding_country)
            if (index == -1) return
            this.countries_option.splice(index, 1)
            this.adding_country = ""
            this.updatePassports(this.user.user)
        },
        savePassportCountries() {
            this.updatePassports(this.user.user);
            console.log(this.user.user.user_id);
            apiUser.editProfile(this.user.user.user_id, this.user.user.firstname, this.user.user.lastname, this.user.user.middlename,
                this.user.user.nickname, this.user.user.primary_email, this.user.user.bio, this.user.user.date_of_birth, this.user.user.gender,
                this.user.user.fitness, this.user.user.additional_email, this.user.user.passports).then((response) => {
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
