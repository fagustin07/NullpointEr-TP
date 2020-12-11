
## Entrega 5 - Investigacion - OrientDB

Durante las semanas de beta-tests recibimos reportes que el juego es una gloria, pero que a la vez, los jugadores se terminan aburriendo porque le falta “algo”. 

Al equipo de desarrollo no le gustó absolutamente nada esta noticia, tantos esfuerzos para que durante la beta del juego los jugadores dieran esta crítica...

![image](https://user-images.githubusercontent.com/57112653/101836411-21f8ab00-3b1c-11eb-88ff-2b380e233b58.png)

Pero luego de una noche de frenesí por parte del equipo surgió la idea la cual nadie había imaginado pero a la vez tan obvia… No siempre lo más simple es lo primero que se nos ocurre…


¡Ahora ganar una pelea tiene sus recompensas! Cuando finaliza un combate, la party ganadora obtiene como recompensa 500 monedas, que podrá gastar en la tienda para comprar accesorio y almacenarlos en su inventario.

![image](https://user-images.githubusercontent.com/57112653/101835856-2d97a200-3b1b-11eb-9d0c-607b0c0fdfc4.png)

<i>(Imagen que demuestra la felicidad del jefe Ronny al ver la nueva integración)</i>

## Servicios

Implementar TiendaService con el siguiente protocolo:

`registrarItem(nombreItem, precio)`: registra un ítem en la tienda. No pueden existir ítems con el mismo nombre.

`itemsEnVenta(): List<Item>` retorna los items que hay en la tienda.

`registrarCompra(nombreParty, nombreAccesorio)`: la party compra el item y lo guarda en su inventario. Una party solo puede comprar un item si tiene suficientes monedas. Luego de la compra, se le descuentan las monedas a la party, y obtiene el item. Debemos persistir la fecha en la que se efectuó la compra.

`loMasComprado() : List<Pair<Item,Int>>` retorna el top 5 de ítems más comprados en los ultimos 7 dias junto con la cantidad de veces que se compró para que los jugadores puedan ver qué es lo que más está a la moda.

`comprasRealizadasPor(nombreParty): List<Item>` retorna todos los items que una party compró alguna vez.

`partiesQueCompraron(nombreAccesorio): List<Party>` retorna todas las parties que alguna vez compraron el ítem.

`tradear(nombrePartyVendedora, nombrePartyCompradora, itemsAVender: List<Item>, monedasOfertadas: Int)`: la primer party vende una lista de items a la segunda party a cambio de las monedas ofrecidas. La vendedora debe tener los items a tradear y la compradora debe tener las monedas que ofreció .

`itemsDe(nombreParty): List<Item>` retorna todos los ítems que posee una party actualmente. Es decir, si compró y luego vendió un item, dicho item no debe aparecer en la lista.
