## Entrega 2 - ORM HIBERNATE - Hito 2

La primera demo fue un exito y todos los testers estan encantados con el sistema de juego. 
Algunos de los reviews recibidos fueron:

"Simple... pero profundo" -  Hideo Kojima

"Literalmente me curo el cancer" - Francisco Perez Ramos

"120 de 100" - IGN

Arman una gran celebracion por el inmediato exito con papafritas, chicitos, gaseosa, sombreros graciosos. 
Todos los desarolladores e ingenieros atienden y comienzan a despotricarse, pero de pronto, la musica se corta y las luces se apagan.
Una singular luz se prende, iluminando al CEO de la empresa, quien con una poderosa voz anuncia:

"Nuestra aventura... solo acaba de comenzar"

Todos emocionados y con lagrimas en los ojos, lanzan los sombreros y se internan freneticamente en sus escritorios, listos para continuar el desarollo de lo que puede llegar a ser el mas grande y significante juego de la historia.



## Funcionalidad

Los testers, si bien contentos, mencionaron que los ayudaria tener acceso a ciertos datos que les permitan mejorar sus estrategias y que sea mas facil ordenar sus parties y aventureros.

Nuestro objetivo para esta proxima iteracion es proveer esos datos para que el equipo del front los visualice.

### Cambios en PeleaService
- Cuando se inicia una pelea, además de nuestra partyId, se recibira el nombre de la party contra la que se pelea.
- Cuando se finaliza una pelea, si nuestra party todavía tiene aventureros vivos, marcar la pelea como ganada, caso contrario, como perdida.
- Cada vez que se resuelve un turno y se genera una nueva habilidad, hay que guardar esa habilidad en la pelea. 
- Cada vez que se recibe una habilidad, hay que guardar esa habilidad en la pelea. 


## Servicios

### AventureroLeaderboardService

- `mejorGuerrero():Aventurero` - Devuelve el aventurero que más daño físico realizó en peleas 
- `mejorMago():Aventurero` - Devuelve el aventurero que más daño mágico realizó en peleas
- `mejorCurandero():Aventurero` - Devuelve el aventurero que más curo en peleas 
- `buda():Aventurero` - Devuelve el aventurero que más meditó en peleas

### PartyService 

- `recuperarOrdenadas(orden:Orden, direccion:Direccion, pagina:Int?):PartyPaginadas` - Devuelve las parties ordenadas en la en la dirección paginadas de a 10. 


###  PeleaService

- `recuperarOrdenadas(partyId:Int, pagina:Int?):PeleasPaginadas` - Devuelve las peleas de una party ordenadas de la mas reciente a la mas vieja de forma paginada de a 10. 


### Se pide:

- Que provean implementaciones para las interfaces descriptas anteriormente.
- Asignen propiamente las responsabilidades a todos los objetos intervinientes, discriminando entre servicios, DAOs y objetos de negocio.
- Provean la implementación de todos los mensajes "TODO" de los DTO
- Creen test que prueben todas las funcionalidades pedidas, con casos favorables y desfavorables.

### Recuerden que:

- No pueden modificar las interfaces ya provistas en el TP, solo implementarlas.
- Pueden agregar nuevos métodos y atributos a los objetos ya provistos, pero no eliminar o renombrar atributos / métodos ya provistos.

