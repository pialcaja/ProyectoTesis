create database if not exists appTransporte;
-- drop database appTransporte;
use appTransporte;

-- =========================================
-- TABLAS
-- =========================================
-- TARJETA_BUS
create table tb_tarjeta_bus(
	id int auto_increment primary key,
    num_tarjeta char(16) unique not null,
    saldo decimal(10,2) not null,
    estado int not null
);
-- ROL
create table tb_rol(
	id int auto_increment primary key,
    nombre varchar(50) unique not null,
    estado int not null
);
-- USUARIO
create table tb_usuario(
	id int auto_increment primary key,
    nombre varchar(50) not null,
    apepa varchar(50) not null,
    apema varchar(50) not null,
    dni char(8) unique not null,
    email varchar(100) unique not null,
    pwd varchar(100) not null,
    estado int not null,
    id_tarjeta int null,
    id_rol int not null,
    constraint fk_usuario_tarjeta_bus foreign key (id_tarjeta) references tb_tarjeta_bus (id),
    constraint fk_usuario_rol foreign key (id_rol) references tb_rol (id)
);
-- LINEA
create table tb_linea(
	id int auto_increment primary key,
    nombre varchar(50) unique not null,
    estado int not null
);
-- RUTA
create table tb_ruta(
	id int auto_increment primary key,
    nombre varchar(50) unique not null,
    estado int not null,
    id_linea int not null,
    constraint fk_ruta_linea foreign key (id_linea) references tb_linea (id)
);
-- PARADERO
create table tb_paradero(
    id int auto_increment primary key,
    nombre varchar(100) not null,
    lat decimal(11, 8) not null,
    lng decimal(11, 8) not null,
    estado int not null,
    sentido tinyint not null, -- 1: Ida, 2: Vuelta
    constraint uq_paradero_coords unique (lat, lng)
);
-- DETALLE(RUTA-PARADERO)
create table tb_ruta_paradero(
    id int auto_increment primary key,
    id_ruta int not null,
    id_paradero int not null,
    orden int not null,
    sentido tinyint not null, -- 1: Ida, 2: Vuelta,
    constraint fk_rp_ruta foreign key (id_ruta) references tb_ruta(id),
    constraint fk_rp_paradero foreign key (id_paradero) references tb_paradero(id)
);
-- MEDIO_PAGO
create table tb_medio_pago(
    id int auto_increment primary key,
    tipo varchar(50) not null,
    descripcion varchar(100) not null,
    numero_enmascarado varchar(20) not null,
    estado int not null,
    id_usuario int not null,
    constraint fk_medio_pago_usuario foreign key (id_usuario) references tb_usuario(id)
);
-- TRANSACCION
create table tb_transaccion(
    id int auto_increment primary key,
    tipo varchar(50) not null,
    monto decimal(10,2) not null,
    fecha datetime not null,
    estado varchar(20) not null,
    id_usuario int not null,
    id_tarjeta int not null,
    id_medio_pago int null,
    constraint fk_transaccion_usuario foreign key (id_usuario) references tb_usuario(id),
    constraint fk_transaccion_tarjeta foreign key (id_tarjeta) references tb_tarjeta_bus(id),
    constraint fk_transaccion_medio_pago foreign key (id_medio_pago) references tb_medio_pago(id)
);

-- =========================================
-- INSERTS
-- =========================================
-- ROL
insert into tb_rol (nombre, estado) values
('ADMIN', 1),
('CLIENTE', 2);

-- USUARIO
insert into tb_usuario (nombre, apepa, apema, dni, email, pwd, estado, id_tarjeta, id_rol) values 
('Piero', 'Caro', 'Jara', '98765432', 'piero@ejemplo.com', '$2a$10$8mo3AYs6oYvYRxWtPiw8m.L0x/m1WRLI8SZdenHWS0YmE5pJXXqjW', 1, null, 1),
('Jennifer', 'Gutierrez', 'Marquez', '12312312', 'jennifer@ejemplo.com', '$2a$10$qXLyPItdgayHh4S9TP3mReoXaNgtKXYXwcL1CVXsFqY0oynrvLDIa', 1, null, 2);

-- TARJETAS DE PRUEBA
insert into tb_tarjeta_bus (num_tarjeta, saldo, estado) values
('1234567890123456', 50.00, 1),
('9876543210987654', 100.00, 1);


update tb_usuario set id_tarjeta = 1 where id = 2;

-- MEDIOS DE PAGO DE PRUEBA (para usuarios que se registren)
-- Estos se insertarán después cuando se creen usuarios clientes de prueba
insert into tb_medio_pago (tipo, descripcion, numero_enmascarado, estado, id_usuario) values 
('Tarjeta', 'Debito VISA', '12341234123412341234', 1, 2);

-- LINEAS
insert into tb_linea (nombre, estado) values
('Corredor azul', 1),
('Corredor rojo', 2),
('Corredor morado', 3);

-- RUTAS
insert into tb_ruta (nombre, estado, id_linea) values
('301', 1, 1),
('305', 1, 1),
('303', 1, 1),
('336', 1, 1),
('370', 1, 1);

-- PARADEROS - SENTIDO IDA (RÍMAC → BARRANCO)
insert into tb_paradero (nombre, lat, lng, estado, sentido) values
('24 de Junio', -12.016510423140467, -77.02980180840994, 1, 1),
('Suarez', -12.017079708034746, -77.030951134936, 1, 1),
('Carlos Valderrama', -12.020923662169666, -77.02953700772312, 1, 1),
('Calle 1', -12.022726949912393, -77.03114099407242, 1, 1),
('Amancaes', -12.026066101424014, -77.03411613905509, 1, 1),
('La Colonia', -12.027845982006186, -77.03255032368416, 1, 1),
('Madrid', -12.029200444345403, -77.03099989309139, 1, 1),
('Leoncio Prado', -12.032565977734537, -77.02846309290642, 1, 1),
('Guardia Republicana', -12.03451237361796, -77.02972775296392, 1, 1),
('Chira', -12.03643399537565, -77.03090216261164, 1, 1),
('Virú', -12.039514341514083, -77.03298365633778, 1, 1),
('Ica', -12.044084618701946, -77.03611843963323, 1, 1),
('Moquegua', -12.046933477427553, -77.03798045675171, 1, 1),
('Ilo', -12.051859851214468, -77.0387670785748, 1, 1),
('Bolivia', -12.05551200513474, -77.03837201162554, 1, 1),
('España', -12.05750030361762, -77.03820736236946, 1, 1),
('Tarma', -12.061035559212296, -77.0378862128373, 1, 1),
('Saco Oliveros', -12.067615013261156, -77.0370806902142, 1, 1),
('Ramón Dagnino', -12.070906388257747, -77.03663163252268, 1, 1),
('Cuba', -12.07422882618025, -77.03610213062878, 1, 1),
('Enrique Villar', -12.076444219688963, -77.0357686256273, 1, 1),
('Manuel Segura', -12.079307016870343, -77.0353350162759, 1, 1),
('Manuel Candamo', -12.082176641708205, -77.03491185357247, 1, 1),
('Tomás Guido', -12.08669785260715, -77.0342515429854, 1, 1),
('Soledad', -12.090412814105933, -77.03371570025418, 1, 1),
('Manuel Bañon', -12.094261067075102, -77.03315955327118, 1, 1),
('Paz Soldán', -12.09650130925236, -77.03283287679854, 1, 1),
('Choquehuanca', -12.098467867990985, -77.03254474072278, 1, 1),
('Raymundo Morales', -12.102701319169595, -77.03184023401018, 1, 1),
('Los Ángeles', -12.106377577242657, -77.03122293541848, 1, 1),
('Andalucía', -12.10870174127519, -77.03082501735364, 1, 1),
('Ayacucho', -12.111422787955487, -77.03034800192354, 1, 1),
('Angamos', -12.113399425219196, -77.03002493747817, 1, 1),
('Piura', -12.115620620347183, -77.02964713165767, 1, 1),
('Pardo', -12.118595477059465, -77.02917281625257, 1, 1),
('Berlín', -12.12217337013307, -77.03117312099421, 1, 1),
('28 de Julio', -12.125180766756667, -77.03300039021873, 1, 1),
('Larco', -12.126863940923183, -77.02979033440344, 1, 1),
('Armendáriz', -12.13125023299553, -77.03002757366325, 1, 1),
('Loyola', -12.133849282247015, -77.02704028839749, 1, 1),
('La Paz', -12.135152554645302, -77.02542442419998, 1, 1),
('El Sol', -12.137660520835082, -77.022611950875, 1, 1),
('Centenario', -12.14079825498939, -77.0223088822953, 1, 1),
('Balta', -12.141857720012606, -77.0185536634698, 1, 1),
('Plaza Butters', -12.143751344343038, -77.01584009149282, 1, 1);

-- PARADEROS - SENTIDO VUELTA (BARRANCO → RÍMAC)
insert into tb_paradero (nombre, lat, lng, estado, sentido) values
('El Sol', -12.13887812865224, -77.01747924794698, 1, 2),
('Chipoco', -12.138066397459525, -77.02233929358415, 1, 2),
('Las Acacias', -12.13285142665688, -77.0232834389376, 1, 2),
('N. Balboa', -12.133353625703712, -77.02748510020456, 1, 2),
('Fanning', -12.130233258639972, -77.02976330761831, 1, 2),
('28 de Julio', -12.127099203691795, -77.02944820620564, 1, 2),
('Benavides', -12.124756805246852, -77.02932136456577, 1, 2),
('Schell', -12.123141610341953, -77.02916306572018, 1, 2),
('Ricardo Palma', -12.120050482890187, -77.02892349170365, 1, 2),
('Piura', -12.116313865131875, -77.02933466736505, 1, 2),
('Angamos', -12.113981016112042, -77.0297248146787, 1, 2),
('Ayacucho', -12.111849054695151, -77.03008795588714, 1, 2),
('Tenaud', -12.109083744835498, -77.0305053514375, 1, 2),
('Río de Janeiro', -12.105938501004108, -77.03109778134481, 1, 2),
('Aramburú', -12.103900659198164, -77.031439462579, 1, 2),
('La Habana', -12.100425103355876, -77.0320335098949, 1, 2),
('Juan de Arona', -12.097429345266447, -77.03251479321767, 1, 2),
('Olavide', -12.093842224971803, -77.03297207662054, 1, 2),
('D. Casanova', -12.089507509616737, -77.03366771733258, 1, 2),
('Tomás Guido', -12.087258694366648, -77.03394191310724, 1, 2),
('Manuel Candamo', -12.082677531939945, -77.03459340959567, 1, 2),
('Manuel Segura', -12.080019251956328, -77.0349787991061, 1, 2),
('E. Villar', -12.07690326661845, -77.03540323113418, 1, 2),
('E. Fernandez', -12.073659203902208, -77.03593462493282, 1, 2),
('Ramón Dagnino', -12.071383984788248, -77.03627646815708, 1, 2),
('Saco Oliveros', -12.06865903582722, -77.03668892972725, 1, 2),
('España', -12.05843657359885, -77.03791337016366, 1, 2),
('Bolivia', -12.056178318019441, -77.03811065431096, 1, 2),
('Quilca', -12.051327802309327, -77.03860872706822, 1, 2),
('Emancipación', -12.046828824411367, -77.03767200682508, 1, 2),
('Callao', -12.043821300279841, -77.0355994168956, 1, 2),
('Pizarro', -12.03915735480286, -77.0324072663713, 1, 2),
('Chira', -12.037579536235473, -77.0313662955595, 1, 2),
('G. Republicana', -12.035432974369726, -77.02991248708324, 1, 2),
('Alcázar', -12.032459767686776, -77.02802346719503, 1, 2),
('La Calezas', -12.029077998722862, -77.03081923297071, 1, 2),
('Arancibia', -12.027369983763254, -77.03268253099132, 1, 2),
('Amancaes', -12.026144739525066, -77.0338615902373, 1, 2),
('Calle 3', -12.023753661485982, -77.0316725235776, 1, 2),
('Carlos Valderrama', -12.02147342809714, -77.02970707008336, 1, 2),
('Calle 10', -12.018859234165916, -77.02837731221673, 1, 2),
('24 de Junio', -12.016511000768713, -77.02979973743331, 1, 2);

-- =========================================
-- RUTA 301 - SENTIDO IDA (1)
-- =========================================
INSERT INTO tb_ruta_paradero (id_ruta, id_paradero, orden, sentido) VALUES
((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='24 de Junio' AND sentido=1), 1, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Suarez' AND sentido=1), 2, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Carlos Valderrama' AND sentido=1), 3, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Calle 1' AND sentido=1), 4, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Amancaes' AND sentido=1), 5, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='La Colonia' AND sentido=1), 6, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Madrid' AND sentido=1), 7, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Leoncio Prado' AND sentido=1), 8, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Guardia Republicana' AND sentido=1), 9, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Chira' AND sentido=1), 10, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Virú' AND sentido=1), 11, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Ica' AND sentido=1), 12, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Moquegua' AND sentido=1), 13, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Ilo' AND sentido=1), 14, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Bolivia' AND sentido=1), 15, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='España' AND sentido=1), 16, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Tarma' AND sentido=1), 17, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Saco Oliveros' AND sentido=1), 18, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Ramón Dagnino' AND sentido=1), 19, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Cuba' AND sentido=1), 20, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Enrique Villar' AND sentido=1), 21, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Manuel Segura' AND sentido=1), 22, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Manuel Candamo' AND sentido=1), 23, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Tomás Guido' AND sentido=1), 24, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Soledad' AND sentido=1), 25, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Manuel Bañon' AND sentido=1), 26, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Paz Soldán' AND sentido=1), 27, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Choquehuanca' AND sentido=1), 28, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Raymundo Morales' AND sentido=1), 29, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Los Ángeles' AND sentido=1), 30, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Andalucía' AND sentido=1), 31, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Ayacucho' AND sentido=1), 32, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Angamos' AND sentido=1), 33, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Piura' AND sentido=1), 34, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Pardo' AND sentido=1), 35, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Berlín' AND sentido=1), 36, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='28 de Julio' AND sentido=1), 37, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Larco' AND sentido=1), 38, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Armendáriz' AND sentido=1), 39, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Loyola' AND sentido=1), 40, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='La Paz' AND sentido=1), 41, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='El Sol' AND sentido=1), 42, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Centenario' AND sentido=1), 43, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Balta' AND sentido=1), 44, 1),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Plaza Butters' AND sentido=1), 45, 1);

-- =========================================
-- RUTA 301 - SENTIDO VUELTA (2)
-- =========================================
INSERT INTO tb_ruta_paradero (id_ruta, id_paradero, orden, sentido) VALUES
((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='El Sol' AND sentido=2), 1, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Chipoco' AND sentido=2), 2, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Las Acacias' AND sentido=2), 3, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='N. Balboa' AND sentido=2), 4, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Fanning' AND sentido=2), 5, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='28 de Julio' AND sentido=2), 6, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Benavides' AND sentido=2), 7, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Schell' AND sentido=2), 8, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Ricardo Palma' AND sentido=2), 9, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Piura' AND sentido=2), 10, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Angamos' AND sentido=2), 11, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Ayacucho' AND sentido=2), 12, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Tenaud' AND sentido=2), 13, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Río de Janeiro' AND sentido=2), 14, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Aramburú' AND sentido=2), 15, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='La Habana' AND sentido=2), 16, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Juan de Arona' AND sentido=2), 17, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Olavide' AND sentido=2), 18, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='D. Casanova' AND sentido=2), 19, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Tomás Guido' AND sentido=2), 20, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Manuel Candamo' AND sentido=2), 21, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Manuel Segura' AND sentido=2), 22, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='E. Villar' AND sentido=2), 23, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='E. Fernandez' AND sentido=2), 24, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Ramón Dagnino' AND sentido=2), 25, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Saco Oliveros' AND sentido=2), 26, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='España' AND sentido=2), 27, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Bolivia' AND sentido=2), 28, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Quilca' AND sentido=2), 29, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Emancipación' AND sentido=2), 30, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Callao' AND sentido=2), 31, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Pizarro' AND sentido=2), 32, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Chira' AND sentido=2), 33, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='G. Republicana' AND sentido=2), 34, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Alcázar' AND sentido=2), 35, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='La Calezas' AND sentido=2), 36, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Arancibia' AND sentido=2), 37, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Amancaes' AND sentido=2), 38, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Calle 3' AND sentido=2), 39, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='El Sol' AND sentido=2), 40, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='Calle 10' AND sentido=2), 41, 2),

((SELECT id FROM tb_ruta WHERE nombre='301'),
 (SELECT id FROM tb_paradero WHERE nombre='24 de Junio' AND sentido=2), 42, 2);
