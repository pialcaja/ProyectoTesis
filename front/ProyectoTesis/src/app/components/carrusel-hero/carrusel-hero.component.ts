import { Component, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-carrusel-hero',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './carrusel-hero.component.html',
    styleUrls: ['./carrusel-hero.component.css']
})
export class CarruselHeroComponent {

    images = [
        'https://corredorazul.pe/wp-content/uploads/2019/05/IMG_2414.jpg',
        'https://corredorazul.pe/wp-content/uploads/2019/05/57068447_1452504824891930_7446981680717365248_n.jpg',
        'https://corredorazul.pe/wp-content/uploads/2019/05/44884505_2138109479776254_6431305911888773120_n.jpg'
    ];

    titles = [
        'MODERNA FLOTA DE BUSES',
        'UN NUEVO SISTEMA',
        'PARADEROS EXCLUSIVOS'
    ];

    subtitles = [
        'para el transporte público de Lima',
        'de Transporte Urbano',
        'para el uso de nuestros pasajeros'
    ];

    currentIndex = signal(0);
    mostrarImagen = signal(true);

    constructor() {
        setInterval(() => this.next(), 4000);
    }

    next() {
        this.mostrarImagen.set(false);

        setTimeout(() => {
            this.currentIndex.update(i => (i + 1) % this.images.length);
            this.mostrarImagen.set(true);
        }, 50);
    }

    getTitle() {
        return this.titles[this.currentIndex()];
    }

    getSubtitle() {
        return this.subtitles[this.currentIndex()];
    }
}

