SELECT 
  * 
FROM 
  ap.solicitud, 
  ap.presupuesto_tarea, 
  ap.tarea, 
  ap.etapa, 
  ap.proyecto
WHERE 
  solicitud.presupuestotareaid = presupuesto_tarea.id AND
  presupuesto_tarea.tareaid = tarea.id AND
  tarea.etapaid = etapa.id AND
  etapa.proyectoid = proyecto.id AND
  proyecto.id = 27;
  
