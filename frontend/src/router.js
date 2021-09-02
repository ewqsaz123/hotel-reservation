
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import RentalManager from "./components/RentalManager"

import PaymentManager from "./components/PaymentManager"

import ManageManager from "./components/ManageManager"


import RentalStatusView from "./components/RentalStatusView"
export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/rentals',
                name: 'RentalManager',
                component: RentalManager
            },

            {
                path: '/payments',
                name: 'PaymentManager',
                component: PaymentManager
            },

            {
                path: '/manages',
                name: 'ManageManager',
                component: ManageManager
            },


            {
                path: '/rentalStatusViews',
                name: 'RentalStatusView',
                component: RentalStatusView
            },


    ]
})
