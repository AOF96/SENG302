import Vue from 'vue';
import Vuetify from 'vuetify/lib';

Vue.use(Vuetify);

export default new Vuetify({
    theme: {
        dark: false,
        options: {
            customProperties: true,
        },
        themes: {
            light: {
                primary: '#1cca92',
                secondary: '#ffffff',
                background: '#ffffff',
                component: '#ffffff',
                colouredComponent: '#3bb18b',
                primaryText: '#000000',
                secondaryText: '#1cca92',
                tertiaryText: '#ffffff',
                success: '#4CAF50',
                warning: '#FFC107',
                error: '#FF5252',
                info: '#2196F3',
            },
            dark: {
                primary: '#1cca92',
                secondary: '#424242',
                background: '#121212',
                component: '#222222',
                colouredComponent: '#238c6a',
                primaryText: '#ffffff',
                secondaryText: '#1cca92',
                tertiaryText: '#222222',
                success: '#4CAF50',
                warning: '#FFC107',
                error: '#FF5252',
                info: '#2196F3',
            }
        }
    }
});
