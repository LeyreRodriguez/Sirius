package com.example.sirius.view.screens


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.android.InternalPlatformTextApi
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.sirius.model.Chat
import com.example.sirius.model.User
import com.example.sirius.navigation.Routes
import com.example.sirius.ui.theme.Green1
import com.example.sirius.ui.theme.Green4
import com.example.sirius.view.components.SingleMessage
import com.example.sirius.viewmodel.ChatViewModel
import com.example.sirius.viewmodel.UserViewModel
import java.util.Locale

@SuppressLint("DiscouragedApi", "CoroutineCreationDuringComposition")
@Composable
fun ChatScreen(NavController: NavHostController,chatViewModel: ChatViewModel, userViewModel : UserViewModel){

    var userList by remember { mutableStateOf<List<User?>>(emptyList()) }
    val user by remember { mutableStateOf(userViewModel.getAuthenticatedUser()) }


    LaunchedEffect(Unit) {
        try {
            userList = userViewModel.getAllUsers()!!
        } catch (e: Exception) {
            Log.e("Error : ", "Error al acceder a la BBDD", e)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {

        Column(modifier = Modifier.fillMaxHeight()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(10.dp)) {

            //    user?.let { myProfile(person = it) { NavController.navigate(route = Routes.PROFILE)} }

                LazyColumn(modifier = Modifier.padding(10.dp)) {
                    items(items = userList, key = { it?.id ?: "" }) { item ->
                        if (item != null) {
                            var lastMessage by remember(item.id) { mutableStateOf<String?>(null) }

                            LaunchedEffect(Unit) {
                                try {
                                    val chatID = user?.let { chatViewModel.generateChatId(it.id, item.id) }
                                    lastMessage = chatID?.let { chatViewModel.getLastMessage(it) }
                                } catch (e: Exception) {
                                    Log.e("Firestore", "Error en ChildList", e)
                                }
                            }




                                UserEachRow(person = item, lastMessage = lastMessage) {
                                    NavController.navigate(Routes.CHAT + "/${item.id}")
                                }

                        }
                    }
                }

            }

        }

    }



}



@Composable
fun myProfile(
    person: User,
    onClick: () -> Unit = {}
)
{
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ){

        Row(
            modifier = Modifier
                .padding(16.dp)
        ) {
            UserImage(imageUrl = person.photoUser, 40.dp,
                Modifier
                    .clip(shape = CircleShape)
                    .size(40.dp))

            Text(
                text  = person.username.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                    ?: "",
                style = TextStyle(
                    fontSize = 20.sp,
                    color = Color.Black
                ),
                modifier = Modifier
                    .padding(start = 10.dp)
                    .align(CenterVertically)
            )

        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .drawBehind {
                    drawLine(
                        color = Color.Black,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1f
                    )
                }
        )
    }


}
@OptIn(InternalPlatformTextApi::class)
@Composable
fun UserEachRow(
    person: User,
   // unseenMessages: List<String>,

    lastMessage: String?,
    onClick: () -> Unit = {},
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ){

        Row(
            modifier = Modifier
                .padding(16.dp)
        ) {
            UserImage(imageUrl = person.photoUser,
                40.dp,
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(8.dp))
                    .size(40.dp))

            Column {
                Text(
                    text  = person.username.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                        ?: "",
                    style = TextStyle(
                        fontSize = 20.sp,
                        color =  Color.Black
                    ),
                    modifier = Modifier
                        .padding(start = 10.dp)
                        //.align(CenterVertically)
                )

                if(lastMessage != null){
                    Text(
                        text = lastMessage?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                            ?: "",
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = Green1
                        ),
                        modifier = Modifier
                            .padding(start = 10.dp)
                    )
                }

            }


        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .drawBehind {
                    drawLine(
                        color = Color.Black,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1f
                    )
                }
        )
    }
}


@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.noRippleEffect(onClick: () -> Unit) = composed {
    clickable(
        interactionSource = MutableInteractionSource(),
        indication = null
    ) {
        onClick()
    }
}



@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun Messages(NavController: NavController, recipientUserId: Int, userViewModel : UserViewModel, chatViewModel : ChatViewModel) {

    val userState = remember { mutableStateOf<User?>(null) }

    LaunchedEffect(UserViewModel) {
        val user = userViewModel.getUserById(recipientUserId)

        userState.value = user
    }

    initRecipientUserId(recipientUserId, chatViewModel)

    val message: String by chatViewModel.message.observeAsState(initial = "")
    val messages: List<Chat> by chatViewModel.messages.observeAsState(
        initial = emptyList<Chat>()
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        //  contentAlignment = Alignment.TopCenter, // Alinea el texto en la parte superior y central
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            //  verticalArrangement = Arrangement.Bottom
        ) {


            userState.value?.let { myProfile(person = it) { NavController.navigate(route = Routes.PROFILE) } }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(weight = 0.85f, fill = true),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                reverseLayout = true
            ) {
                items(messages) { message ->
                    SingleMessage(
                        message = message.message,
                        isCurrentUser = message.sentBy == userViewModel.getAuthenticatedUser()?.id ?: 0
                    )
                }
            }
            Box(
               //  modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                OutlinedTextField(
                    value = message,
                    onValueChange = {
                        chatViewModel.updateMessage(it)
                        // message = it
                    },
                    label = {
                        Text(
                            "Escribe tu mensaje"
                        )
                    },
                    maxLines = 5,
                    modifier = Modifier
                        .padding(horizontal = 15.dp, vertical = 1.dp)
                        .fillMaxWidth(),
                     //   .weight(weight = 0.09f, fill = true),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                chatViewModel.addMessage(recipientUserId)
                                chatViewModel.updateMessage("")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Boton de enviar",
                                tint = Green4
                            )
                        }
                    }
                )
            }
        }
    }
}



fun initRecipientUserId(recipientUserId: Int, chatViewModel: ChatViewModel) {
    chatViewModel.initRecipientUserId(recipientUserId)
}




