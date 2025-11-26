export interface ParaderoDTO {
  id: number;
  nombre: string;
  lat: number;
  lng: number;
}

export interface MejorRutaResponseDTO {
  mensaje?: string;
  origen?: ParaderoDTO;
  destino?: ParaderoDTO;
  rutaId?: number;
  rutaNombre?: string;
  sentido?: string;
  distanciaOrden?: number;
  paraderosRuta?: ParaderoDTO[];
}
