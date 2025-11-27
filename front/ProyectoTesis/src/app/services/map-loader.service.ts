import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class MapLoaderService {
  private loaded = false;

  load(apiKey: string, libraries: string[] = []): Promise<void> {
    if (this.loaded) return Promise.resolve();

    return new Promise((resolve, reject) => {
      const script = document.createElement('script');
      script.src = `https://maps.googleapis.com/maps/api/js?key=${apiKey}&libraries=${libraries.join(',')}`;
      script.async = true;
      script.defer = true;
      script.onload = () => { this.loaded = true; resolve(); };
      script.onerror = (err) => reject(err);
      document.head.appendChild(script);
    });
  }
}
