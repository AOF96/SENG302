<template>
<div id="settingsWrap">
    <UserSettingsMenu />
    <div>
        <h2>Passport Countries</h2>
        <div v-for="country in passport_countries" v-bind:key="country">
            <h4>{{country}}</h4>
            <button v-on:click="removePassportCountries(country)">remove</button>
        </div>

        <div>
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
          </form>
        </div>
    </div>
</div>
</template>

<script>
import UserSettingsMenu from '@/components/Settings/UserSettingsMenu'
import {
    userInfo
} from "../../globals";
import axios from "axios";
// const SERVER_URL = 'https://4967d4f4-8301-42d1-a778-e3d150633644.mock.pstmn.io';
const COUNTRIES_URL = 'https://restcountries.eu/rest/v2/all?fields=name'

export default {
    components: {
        UserSettingsMenu
    },
    data() {
        return {
            passport_countries: userInfo.passportCountries,
            countries_option: [],
            adding_country: "",
            num_of_countries: 1,
        }
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

                for(let country of this.passport_countries) {
                    const index = countries.indexOf(country)
                    if (index == -1) continue
                    countries.splice(index, 1)
                }
                this.countries_option = countries
            })
            .catch(error => console.log(error))
    },
    methods: {
        update() {
            userInfo.passportCountries = this.passport_countries
            this.adding_country = ""
        },
        removePassportCountries(country) {
            const index = this.passport_countries.indexOf(country)
            if (index == -1) return
            this.passport_countries.splice(index, 1)
            this.countries_option.push(country)
            this.countries_option.sort()
            this.update()
        },
        addPassportCountries() {
            if(!this.adding_country) return
            // axios.post(SERVER_URL + '/editPassportCountries', {
            //         passportCountries: this.adding_country //should be matched the column name
            //     })
            //     .then((response) => {
            //         console.log(response.data)
            //     }, (error) => {
            //         console.log(error)
            //     })
            this.passport_countries.push(this.adding_country)
            const index = this.countries_option.indexOf(this.adding_country)
            if (index == -1) return
            this.countries_option.splice(index, 1)
            this.update()
        },
    }
}
</script>
