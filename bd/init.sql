create database if not exists appTransporte;
-- drop database appTransporte;
use appTransporte;

-- TABLAS
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
    nombre varchar(100) unique not null,
    lat decimal(10, 7) not null,
    lng decimal(10, 7) not null,
    estado int not null,
    constraint uq_paradero_coords unique (lat, lng)
);

-- DETALLE(RUTA-PARADERO)
create table tb_ruta_paradero(
    id int auto_increment primary key,
    id_ruta int not null,
    id_paradero int not null,
    orden int not null,
    constraint fk_rp_ruta foreign key (id_ruta) references tb_ruta(id),
    constraint fk_rp_paradero foreign key (id_paradero) references tb_paradero(id)
);
