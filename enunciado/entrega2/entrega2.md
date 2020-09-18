## Entrega 2 - ORM HIBERNATE - Hito 1

## Cambios desde el TP anterior

Se identificaron una serie de cambios necesarios a hacerse sobre la prueba de concepto anterior:  
La capa de persistencia deberá cambiarse para utilizar Hibernate/JPA en lugar de JDBC.  
**Nota:** No es necesario que mantengan los test y funcionalidad utilizando JDBC.

## Funcionalidad

La prueba de concepto fue un éxito! Pero aún es muy temprano para festejar.  
Nos reunimos con el líder del equipo técnico, y nos explica que estamos listos para centrarnos en implementar lo que va a ser la base principal del juego.  
Para ello, nos citan tanto a nosotros como a todo el equipo de desarrolladores a un cómodo cuarto con facturas, café, y un proyector.  
Al llegar, el líder técnico no tarda en comenzar con su presentación, comenzando por contarnos a grandes rasgos de que tratara el juego:

Alguna imagencita

EPERS tactics es un juego de batalla por turnos, en el cual se enfrentaran dos o varios grupos de aventureros, unos contra los otros.  
En el, se podrán crear grupos de aventureros a los cuales llamaremos "Party".

Cada uno de los aventureros podra poseer atributos, habilidades y características que los distingan entre si, de tal forma que cada aventurero en una party pueda  
utilizarse para cumplir un determinado rol; ya sea ser un Guerrero con gran defensa que protege a los más débiles, o un brujo con poderosos hechizos que destruya al enemigo.

Las batallas serán resueltas de manera automática por un poderoso servidor de batallas online que está implementando un equipo hermano.  
Lo que es importante para nosotros, es que estas peleas utilizaran los datos provistos por nuestro proyecto.

Para poder realizar estas batallas de forma automática, cada personaje tendrá sus propias tácticas, las cuales utilizara para determinar como se comportara en batalla.

### Partys

Una party de aventureros debe poseer:

- Nombre:String
- ImagenUrl:String
- aventureros:List<Aventurero> Hasta 5 aventureros como máximo
- enPelea:Boolean
- Ubicacion ??

Una Party se crea sin aventureros y no esta en ninguna ubicación.

### Aventureros

Un aventurero debe poseer:

- Nombre:String
- Nivel:String
- Atributos:Atributos
- Estadisticas??
- Tacticas: List<Tactica>
- ImagenUrl:String

#### Nivel

Representa el nivel de experiencia del aventuro en su profesión.  
Todos los aventureros son creados con nivel 1.  
Por ahora no nos vamos a preocupar de como los aventureros aumentan su nivel.

#### Atributos

Los atributos proporcionan una breve descripción de las características físicas y mentales de cada aventurero:

Estos atributos son:

- Fuerza: mide la potencia física y capacidades deportivas.
- Destreza: mide la agilidad y los reflejos.
- Constitución: mide la resistencia y la salud.
- Inteligencia: mide el razonamiento y la memoria.

¿Es un aventurero musculoso y perspicaz? ¿Brillante y encantador? ¿Ágil y robusto? Las puntuaciones  
de atributos definen esas cualidades y muestran en nuestros aventureros sus factores positivos así como  
debilidades.

Las puntaciones de los atributos pueden ir del 1 al 100 como máximo.

#### Estadísticas

Las estadísticas son valores de interés para el funcionamiento de nuestros aventureros.

¿Como nos beneficia a la hora de combatir tener la fuerza de un gigante? ¿Que desventajas puede tener ser lento y torpe?  
Los valores de las estadísticas nos brindan una forma de cuantificar de forma numérica tal respuesta.

Las estadísticas que nos interesan son:

- Vida: Representa la cantidad de impacto físico y mágico que puede recibir nuestro aventurero antes de perecer. De estar llena, representa que nuestro personaje esta en pleno estado físico y mental. Cuando esta repleta, el personaje ha muerto.

El valor de la vida se calcula con la siguiente formula: Nivel del aventurero x5 + Constitucion x 2 + Fuerza

- Armadura: Representa la capacidad de resistencia física de nuestro personaje. De ser alta, será mas dificil para el enemigo causar daño físico en el aventurero.

El valor de la armadura se calcula con la siguiente formula: Nivel de aventurero + Constitucion

- Mana: Representa la cantidad de energía mágica que un personaje tiene disponible. Varias habilidades y hechizos requieren la utilización de esta energía para manifestarse de forma material y poder actuar en favor del aventurero.

El mana se calcula con la siguiente formula: Nivel de aventurero + Inteligencia

- Velocidad: Se utiliza para determinar los reflejos a la hora de esquivar un ataque, y que tan rápido actuara el aventurero en batalla.

La velocidad se calcula con la siguiente formula: Nivel de aventurero + Destreza

- Daño físico: Utilizado a la hora de determinar cuanto daño es inflingido a un oponente con ataques físicos.

El daño físico se calcula con la siguiente formula: Nivel del aventurero + Fuerza + Destreza/2

- Poder mágico: Utilizado a la hora de determinar cuanto daño es inflingido a un oponente con ataques mágicos, o cuanta vitalidad recupera un aliado cuando es curado.

El Poder mágico se calcula con la siguiente formula: Nivel del aventurero + Inteligencia

- Precisión física: Utilizado a la hora de determinar si un ataque da con el enemigo.

La precisión física se calcula con la siguiente formula: Nivel del aventurero + Fuerza + Destreza

#### Habilidades

Las Habilidades representan las interacciones que realizan los personajes entre si, sean positivas y negativas.

Un Pícaro podrá querer atacar con su daga al mago enemigo, o un clérigo podrá querer curar a un aliado con su magia.

Las habilidades representan entonces acciones que realiza un personaje (emisor de la habilidad) sobre otro (receptor de la habilidad).

Nos interesa modelar las siguientes acciones que pueden emitir los aventureros:

- Atacar: Se realiza un ataque contra el objetivo. Nos interesa saber sobre el ataque:
  - Daño físico del emisor.
  - Precisión física del emisor.
  - receptor.
- Defender: El aventurero se coloca en acción defensiva, protegiendo a sus aliados del peligro. Nos interesa saber sobre la defensa:
  - El emisor de la acción
  - Receptor  
    Además, cuando un aventurero defiende solo sufre la mitad del daño que recibe hasta su próximo turno.
- Curar: Implorando la ayuda de alguna deidad divina, el aventurero cura las heridas de sus aliados. Nos interesa saber sobre la curación:

  - El poder mágico del emisor
  - receptor  
    Cuando un aventurero cura, pierde 5 puntos de mana.

- Atacar con Magia ofensiva: Luego de una incautación arcana, el aventurero invoca el poder arcano para dañar a sus enemigos. Nos interesa saber sobre el ataque con magia ofensiva:
  - El poder mágico del emisor
  - nivel del emisor
  - receptor  
    Cuando un aventurero ataca con magia, pierde 5 puntos de mana.
- Meditar: El aventurero cierra sus ojos y se concentra en absorber el mana de los alrededores. Nos nos interesa saber sobre meditar:
  - receptor(en este caso, es siempre el emisor).

También así, nos interesa modelar como se comportan estas habilidades sobre el aventurero receptor:

- Atacar: Cuando un aventurero recibe un ataque, debe realizar el siguiente calculo para saber si el ataque fue exitoso:  
  `Ataque Exitoso = (random(1, 20)) + Precisión física del emisor >= Armadura del receptor + velocidad/2`  
  Si el ataque fue exitoso, el receptor recibe daño igual al `Daño físico del emisor`
- Defender: Cuando un aventurero es defendido, por los próximos 3 turnos si ha de recibir algún ataque de cualquier tipo, este Serra recibido por el defensor.
- Curar: Cuando un aventurero es curado, este recupera puntos de vida igual al `poder mágico del emisor`. Los punto de vida no pueden superar el máximo de vida del aventurero.
- Atacar con Magia ofensiva: Cuando un aventurero recibe un ataque, debe realizar el siguiente calculo para saber si el ataque fue exitoso:  
  `Ataque Exitoso = (random(1, 20))+ nivel del emisor >= velocidad/2`  
  Si el ataque fue exitoso, el receptor recibe daño igual al `El poder mágico del emisor`
- Meditar: El aventurero recupera mana igual a su nivel.

#### Tácticas

### Peleas

Las peleas serán realizadas por un servidor externo que un equipo hermano estará desarrollando.  
Para llevarlas a cabo, el servidor externo utilizara la velocidad de los aventureros para determinar un orden de turnos.  
Luego, en base a la velocidad de los aventureros, el servidor le dará al backend el listado de enemigos y el aventurero que debe actuar.  
El backend debe utilizar las tácticas de ese aventurero para seleccionar una acción a realizar contra que receptor, y en base a eso el servidor de peleas se encargara de hacerle pedir al grupo al cual pertenece el receptor de esa acción que la ejecute.

La pelea continuara hasta que solo un equipo quede vivo.

## DTO

Una vez terminadas las charlas sobre el juego, el líder técnico cambia de diapositivas y nos comenta sobre grandes avances que estuvo realizando el equipo de Frontend los cuales ya poseen una interfaz semi-funcional lista como prototipo.  
 Dado que ellos ya poseen una implementación de ciertos objetos de negocio, nos comenta que se nos va a proveer un DTO (Data Transfer Object) que encapsula la representación de datos que el equipo de frontend están utilizando, y nos pide que una vez implementado este concepto en el backend proveamos la funcionalidad que transforme esa representación en una que se maneje el backend.

## Servicios

Se pide que implementen los siguientes servicios los cuales serán consumidos por el frontend de la aplicación.

### PartyService

- `crear(party:Party):Party`
- `recuperar(partyId:Long):Party`
- `listar():List<Party>`
- `actualizar(party:Party):Party`
- `eliminar(partyId:Long)`??

### AventureroService

- `crear(partyId:Long, aventurero:Aventurero):Aventurero` Crea un aventurero y lo agrega a la party
- `recuperar(aventureroId:Long):Aventurero`
- `listar(partyId:Long):List<Party>` Devuelve los aventureros de la party
- `actualizar(aventurero:Aventurero):Aventurero`
- `eliminar(aventureroId:Long)`

### PeleaService

- `iniciarPelea(idParty: Long):Pelea`
- `resolverTurno(idPelea:Long, idAventurero:Long, enemigos: List<Aventurero>) : Habilidad` - Dada la lista de enemigos, el aventurero debe utilizar sus Tacticas para elegir que habilidad utilizar sobre que receptor.
- `recibirHabilidad(idPelea:Long, idAventurero:Long, habilidad: Habilidad):Aventurero` - El aventurero debe resolver la habilidad que esta siendo ejecutada sobre el.
- `terminarPelea(idPelea:Long):Pelea`

### Se pide:

- Que provean implementaciones para las interfaces descriptas anteriormente.
- Que modifiquen el mecanismo de persistencia de Party y Aventurero de forma de que todo el modelo persistente utilice Hibernate.
- Asignen propiamente las responsabilidades a todos los objetos intervinientes, discriminando entre servicios, DAOs y objetos de negocio.
- Provean la implementación al mensaje "aModelo" de los DTO
- Creen test que prueben todas las funcionalidades pedidas, con casos favorables y desfavorables.
- Que los tests sean deterministicos. Hay mucha lógica que depende del resultado de un valor aleatorio. Se aconseja no utilizar directamente generadores de valores aleatorios (random) sino introducir una interfaz en el medio para la cual puedan proveer una implementación mock determinística en los tests.

### Recuerden que:

- No pueden modificar las interfaces ya provistas en el TP, solo implementarlas.
- Pueden agregar nuevos métodos y atributos a los objetos ya provistos, pero no eliminar o renombrar atributos / métodos ya provistos.

### Consejos útiles:

- Finalicen los métodos de los services de uno en uno. Que quiere decir esto? Elijan un service, tomen el método más sencillo que vean en ese service, y encárguense de desarrollar la capa de modelo, de servicios y persistencia solo para ese único método. Una vez finalizado (esto también significa testeado), pasen al próximo método y repitan.
- Cuando tengan que persistir con hibernate, analicen:  
  Qué objetos deben ser persistentes y cuáles no?  
  Cuál es la cardinalidad de cada una de las relaciones? Como mapearlas?
