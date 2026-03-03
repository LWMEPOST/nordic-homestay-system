import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import 'vant/lib/index.css'
import { Button, Tabbar, TabbarItem, NavBar, Form, Field, CellGroup, Cell, List, Card, Tag, Image as VanImage, Swipe, SwipeItem, Icon, Toast, Dialog, Calendar, RadioGroup, Radio, SubmitBar, Popup, Stepper } from 'vant'

const app = createApp(App)

app.use(createPinia())
app.use(router)

// Register Vant Components
app.use(Button)
app.use(Tabbar).use(TabbarItem)
app.use(NavBar)
app.use(Form).use(Field).use(CellGroup).use(Cell)
app.use(List).use(Card).use(Tag).use(VanImage)
app.use(Swipe).use(SwipeItem)
app.use(Icon)
app.use(Toast).use(Dialog)
app.use(Calendar)
app.use(RadioGroup).use(Radio)
app.use(SubmitBar)
app.use(Popup)
app.use(Stepper)

app.mount('#app')
