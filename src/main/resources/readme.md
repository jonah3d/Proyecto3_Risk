# Connection
Para empezar, la aplicación está diseñada para ejecutarse en un proveedor de servicios en la nube utilizando Docker. El uso de Docker aquí sirve para encapsular __el build__ de la aplicación y aislarla, de modo que el proveedor en la nube pueda ejecutarla por nosotros.

Si tienes la intención de ejecutarla localmente, puedes hacerlo simplemente cambiando algunas propiedades en el archivo `application.properties`. Necesitas el archivo de base de datos, que se encuentra en la carpeta `P3_Server_Data` bajo el nombre `risk_db`. El esquema de la base de datos fue creado utilizando MySQL 9.


# Web Socket Request

#### 1. Register Session
```json
{
  "action": "register",
  "userId": 7  
}
 ```


#### 2. Create Game
```json
{
  "action": "create_game",
  "maxPlayers": 2,
  "isPublic": true,
  "gameName":"fucking best"
}
```
#### 3. List Available Games
```json
{
  "action": "list_games"
}
```

#### 4. Join Game
```json
{
  "action": "join_game",
  "token": "9d5a007e"
}
```

#### 5. Send Game Input
```json
{
  "action": "send_input",
  "data": {
    "type": "occupation",
    "countryId": 20,
    "troops": 1
  }
}
```

### 6. BONUS
```json
{
  "action": "send_input",
  "data": {
    "type": "place_troops",
	"countryId" : 14,
	"troops" : 1
  }
}
```

### 7. Attacking 
```json
{
    {
    "action": "send_input",
    "data": {
    "type": "attack",
    "countryId": 12,
    "enemyCountryId": 14,
    "troops": 2
    }
	
}
```

### 8. Move Troops 
```json
{
"action": "send_input",
    "data":{
    "type": "move_troops",
    "troops": 3
    }
	
}
```

### 9. End Attacking 
```json
{
"action": "send_input",
"data":{
  "type": "end_attack"
}	
}

```

### 10. Fortify
```json
{
"action": "send_input",
"data":{
  "type": "fortify",
  "sourceCountryId": 2,
  "targetCountryId": 8,
  "troops": 2
}
}
```

### 11. End Turn
```json
{
"action": "send_input",
"data":{
  "type": "end_turn"
}
	
}
```


### 12. Leave Game 
```json
{
  "action": "leave_game"
}
```


