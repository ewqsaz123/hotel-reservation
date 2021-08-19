
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import ReservationManager from "./components/ReservationManager"

import PaymentManager from "./components/PaymentManager"

import RoomManagementManager from "./components/RoomManagementManager"


import ReservationInfo from "./components/ReservationInfo"
import RoomReservationInfo from "./components/RoomReservationInfo"
import ResevationStatusView from "./components/ResevationStatusView"
export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/reservations',
                name: 'ReservationManager',
                component: ReservationManager
            },

            {
                path: '/payments',
                name: 'PaymentManager',
                component: PaymentManager
            },

            {
                path: '/roomManagements',
                name: 'RoomManagementManager',
                component: RoomManagementManager
            },


            {
                path: '/reservationInfos',
                name: 'ReservationInfo',
                component: ReservationInfo
            },
            {
                path: '/roomReservationInfos',
                name: 'RoomReservationInfo',
                component: RoomReservationInfo
            },
            {
                path: '/resevationStatusViews',
                name: 'ResevationStatusView',
                component: ResevationStatusView
            },


    ]
})
