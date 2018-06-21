var bus = new Vue()

function emitError(message) {
    bus.$emit('error', message)
}

function onError(fn) {
    bus.$on('error', fn)
}

function emitInfo(message) {
    bus.$emit('info', message)
}

function onInfo(fn) {
    bus.$on('info', fn)
}

function emitLogin() {
    bus.$emit('login')
}

function onLogin(fn) {
    bus.$on('login', fn)
}

function handleError(errorMessage) {
    emitError(errorMessage)
}

var Alert = {
    template: '#alert',
    data: function () {
        return {
            classObject: {
                'is-danger': false,
                'is-info': false
            },
            message: '',
            visible: false
        }
    },
    mounted: function () {
        onError(function (errorMessage) {
            this.classObject['is-danger'] = true
            this.message = errorMessage
            this.visible = true
            setTimeout(this.reset.bind(this), 2000)
        }.bind(this))

        onInfo(function (message) {
            this.classObject['is-info'] = true
            this.message = message
            this.visible = true
            setTimeout(this.reset.bind(this), 2000)
        }.bind(this))
    },
    methods: {
        reset: function () {
            this.visible = false
            this.classObject = {}
        }
    }
}

var Header = {
    template: '#header',
    data: function () {
        return {
            currentUser: JSON.parse(sessionStorage.getItem('currentUser')),
            to: '/'
        }
    },
    computed: {
        to: () => sessionStorage.getItem('authority')
    },
    mounted: function () {
        onLogin(function () {
            this.currentUser = JSON.parse(sessionStorage.getItem('currentUser'))
            this.to = sessionStorage.getItem('authority')
        }.bind(this))
    },
    methods: {
        logout: function () {
            this.currentUser = null
            sessionStorage.removeItem('token')
            sessionStorage.removeItem('currentUser')
            sessionStorage.removeItem('authority')
            this.$router.push('/')
        }
    }
}

var PostList = {
    template: '#posts',
    data: function () {
        return {
            posts: []
        }
    },
    filters: {
        longToDate: function (long) {
            var date = new Date(long)
            return date.toLocaleString()
        }
    },
    mounted: function () {
        axios.get('/api/post').then(function (res) {
            if (res.data.error) {
                handleError(res.data.error)
            } else {
                var posts = res.data
                this.posts = posts.sort(function (a, b) {
                    return b.createTime - a.createTime
                })
            }
        }.bind(this))
    },
    methods: {
        showDetail: {}
    }
}

var LoginForm = {
    template: '#login-form',
    data: function () {
        return {
            name: '',
            password: ''
        }
    },
    methods: {
        handleSubmit: function (e) {
            e.preventDefault()
            if (this.name === '') {
                handleError('用户名不能为空')
                return false
            }
            if (this.password === '') {
                handleError('密码不能为空')
                return false
            }
            axios.post('/api/authentication', {
                name: this.name,
                password: this.password
            }).then(function (res) {
                if (res.data.error) {
                    handleError(res.data.error)
                } else {
                    sessionStorage.setItem('token', res.data.token)
                    sessionStorage.setItem('currentUser', JSON.stringify(res.data.user))
                    switch (res.data.authority) {
                        case "Admin":
                            console.log('admin login')
                            sessionStorage.setItem('authority', "/admin")
                            emitLogin()
                            this.$router.push('/admin')
                            break
                        default:
                            console.log('normal user login')
                            sessionStorage.setItem('authority', "/user")
                            emitLogin()
                            this.$router.push('/user')
                            break
                    }
                    this.$router.go(0)
                }
            }.bind(this))
        }
    }
}

// var SignupForm = {
//     template: '#signup-form',
//     data: function () {
//         return {
//             name: '',
//             password: '',
//             passwordAgain: ''
//         }
//     },
//     methods: {
//         handleSubmit: function (e) {
//             e.preventDefault()
//             if (this.name === '') {
//                 handleError('用户名不能为空')
//                 return false
//             }
//             if (this.password === '') {
//                 handleError('密码不能为空')
//                 return false
//             }
//             if (this.password !== this.passwordAgain) {
//                 handleError('两次输入的密码不一致')
//                 return false
//             }
//             axios.post('/api/user/add', {
//                 name: this.name,
//                 password: this.password
//             }).then(function (res) {
//                 if (res.data.error) {
//                     handleError(res.data.error)
//                 } else {
//                     emitInfo('注册成功，请登录')
//                     this.$router.push('/login')
//                 }
//             }.bind(this))
//         }
//     }
// }

var User = {
    template: '#user',
    data: function () {
        currentUser = JSON.parse(sessionStorage.getItem('currentUser'))
        return {
            image: currentUser.id + '.jpg',
            currentUser: currentUser
        }
    },
    // computed: {
    //     image: () => JSON.parse(sessionStorage.getItem('currentUser')).id + '.jpg'
    // },
    beforeRouteEnter: function (to, from, next) {
        if (sessionStorage.getItem('token') === null) {
            next({
                path: 'login'
            })
            emitInfo('请先登录')
        } else {
            next()
        }
    },
    methods: {
        upload: function (e) {
            e.preventDefault()
            console.log(e.target.files)
            var files = e.target.files
            var data = new FormData()
            data.append('image', files[0])
            axios.post('/api/user/uploadImage', data, {
                headers: {
                    token: sessionStorage.getItem('token')
                }
            }).then(function (res) {
                if (res.data.error) {
                    handleError(res.data.error)
                } else {
                    this.$router.go(0)
                    emitInfo('更新头像成功')
                }
            }.bind(this))
        }
    }
}

var Admin = {
    template: '#admin',
    data: function () {
        return {
            infoAll: [],
            searchText: '',
        }
    },
    computed: {
        filteredInfoAll: function () {
            var searchText = this.searchText;
            var infoAll = this.infoAll;
            if (searchText == '') {
                return infoAll
            }
            return infoAll.filter(function (item) {
                return item.name.toLowerCase().indexOf(searchText.toLowerCase()) != -1
            });;
        }
    },
    mounted: function () {
        axios.get('/api/admin/infoall', {
            headers: {
                token: sessionStorage.getItem('token')
            }
        }).then(function (res) {
            if (res.data.error) {
                handleError(res.data.error)
            } else {
                console.log(res.data)
                this.infoAll = []
                var users = res.data.users
                var authorities = res.data.authorities
                for (var i = 0, j = users.length; i < j; i++) {
                    this.infoAll[i] = users[i]
                    this.infoAll[i].authority = authorities[i]
                }
                console.log(this.infoAll)
            }
        }.bind(this))
    },
    beforeRouteEnter: function (to, from, next) {
        if (sessionStorage.getItem('token') === null) {
            next({
                path: 'login'
            })
            emitInfo('请先登录')
        } else {
            next()
        }
    },
    methods: {
        upload: function (e) {
            e.preventDefault()
            console.log(e.target.files)
            var files = e.target.files
            var data = new FormData()
            data.append('userInfo', files[0])
            axios.post('/api/admin/add', data, {
                headers: {
                    token: sessionStorage.getItem('token')
                }
            }).then(function (res) {
                if (res.data.error) {
                    handleError(res.data.error)
                } else {
                    this.$router.go(0)
                    emitInfo('添加用户成功')
                }
            }.bind(this))
        }
    }
}

var AdminInfoMgr = {
    template: '#admin-info-mgr',
    data: function () {
        editUser = JSON.parse(sessionStorage.getItem('editUser'))
        return {
            editUser: editUser ? editUser : {
                name: '',
                studentID: ''
            },
            image: 'null.jpg',
            age: 0,
            department: '',
            major: '',
            grade: '',
            classNum: '',
            phone: '',
            gender: '',
            authority: 'Student'
        }
    },
    mounted: function () {
        // console.log("mounted enter")
        axios.get('/api/admin/info/' + this.$route.params.id, {
            headers: {
                token: sessionStorage.getItem('token')
            }
        }).then(function (res) {
            if (res.data.error) {
                handleError(res.data.error)
            } else {
                console.log(res.data)
                this.editUser = res.data.user
                this.image = this.editUser.id + '.jpg'
                this.age = this.editUser.age
                this.department = this.editUser.department
                this.major = this.editUser.major
                this.grade = this.editUser.grade
                this.classNum = this.editUser.classNum
                this.phone = this.editUser.phone
                this.gender = this.editUser.gender
                this.authority = res.data.authority
                sessionStorage.setItem('editUser', JSON.stringify(res.data))
            }
        }.bind(this))
    },
    beforeRouteEnter: function (to, from, next) {
        if (sessionStorage.getItem('token') === null) {
            next({
                path: 'login'
            })
            emitInfo('请先登录')
        } else {
            next()
        }
    },
    methods: {
        handleSubmit: function (e) {
            e.preventDefault()
            // console.log(this)
            axios.post('/api/admin/update/' + this.$route.params.id, {
                name: JSON.parse(sessionStorage.getItem('editUser')).name,
                age: this.age,
                department: this.department,
                major: this.major,
                grade: this.grade,
                classNum: this.classNum,
                phone: this.phone,
                gender: this.gender,
                authority: this.authority
            }, {
                headers: {
                    token: sessionStorage.getItem('token')
                }
            }).then(function (res) {
                if (res.data.error) {
                    handleError(res.data.error)
                } else {
                    emitInfo('更新信息成功')
                    editUser = JSON.parse(sessionStorage.getItem('editUser'))
                    editUser.age = this.age
                    editUser.department = this.department
                    editUser.major = this.major
                    editUser.grade = this.grade
                    editUser.classNum = this.classNum
                    editUser.phone = this.phone
                    editUser.gender = this.gender
                    sessionStorage.setItem('editUser', JSON.stringify(editUser))
                }
            }.bind(this))
        }
    }
}

var AdminDelete = {
    template: '#admin-delete',
    created: function () {
        console.log("created enter")
        axios.get('/api/admin/delete/' + this.$route.params.id, {
            headers: {
                token: sessionStorage.getItem('token')
            }
        }).then(function (res) {
            if (res.data.error) {
                handleError(res.data.error)
            } else {
                emitInfo('删除成功')
                this.$router.push('/admin')
            }
        }.bind(this))
    },
    beforeRouteEnter: function (to, from, next) {
        if (sessionStorage.getItem('token') === null) {
            next({
                path: 'login'
            })
            emitInfo('请先登录')
        } else {
            next()
        }
    }
}

var InfoMgr = {
    template: '#info',
    data: function () {
        currentUser = JSON.parse(sessionStorage.getItem('currentUser'))
        return {
            image: currentUser.id + '.jpg',
            age: currentUser.age,
            department: currentUser.department,
            major: currentUser.major,
            grade: currentUser.grade,
            classNum: currentUser.classNum,
            phone: currentUser.phone,
            gender: currentUser.gender,
            currentUser: currentUser
        }
    },
    mounted: function () {
        // console.log("mounted enter")
        axios.get('/api/user/info', {
            headers: {
                token: sessionStorage.getItem('token')
            }
        }).then(function (res) {
            if (res.data.error) {
                handleError(res.data.error)
            } else {
                // console.log(res.data)
                this.currentUser = res.data
                sessionStorage.setItem('currentUser', JSON.stringify(res.data))
            }
        }.bind(this))
    },
    beforeRouteEnter: function (to, from, next) {
        if (sessionStorage.getItem('token') === null) {
            next({
                path: 'login'
            })
            emitInfo('请先登录')
        } else {
            next()
        }
    },
    methods: {
        handleSubmit: function (e) {
            e.preventDefault()
            // console.log(this)
            axios.post('/api/user/update', {
                name: JSON.parse(sessionStorage.getItem('currentUser')).name,
                age: this.age,
                department: this.department,
                major: this.major,
                grade: this.grade,
                classNum: this.classNum,
                phone: this.phone,
                gender: this.gender
            }, {
                headers: {
                    token: sessionStorage.getItem('token')
                }
            }).then(function (res) {
                if (res.data.error) {
                    handleError(res.data.error)
                } else {
                    emitInfo('更新信息成功')
                    currentUser = JSON.parse(sessionStorage.getItem('currentUser'))
                    currentUser.age = this.age
                    currentUser.department = this.department
                    currentUser.major = this.major
                    currentUser.grade = this.grade
                    currentUser.classNum = this.classNum
                    currentUser.phone = this.phone
                    currentUser.gender = this.gender
                    sessionStorage.setItem('currentUser', JSON.stringify(currentUser))
                }
            }.bind(this))
        }
    }
}

var defaultIndex = {
    template: '#index',
}

var routes = [{
        path: '/',
        component: defaultIndex
    },
    {
        path: '/login', // 登录页面
        component: LoginForm
    },
    // {
    //     path: '/signup', // 注册
    //     component: SignupForm
    // },
    {
        path: '/user', // 学生和老师主页
        component: User
    },
    {
        path: '/admin', // 管理员主页
        component: Admin
    },
    {
        path: '/admin/info/:id',
        component: AdminInfoMgr
    },
    {
        path: '/admin/delete/:id',
        component: AdminDelete
    },
    {
        path: '/info', // 查看与修改个人信息
        component: InfoMgr
    },
]

var router = new VueRouter({
    routes: routes
})

new Vue({
    router: router,
    components: {
        'x-header': Header,
        'x-alert': Alert,
    }
}).$mount('#app')