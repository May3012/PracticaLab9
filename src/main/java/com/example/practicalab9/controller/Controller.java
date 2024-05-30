package com.example.practicalab9.controller;

import com.example.practicalab9.entity.Player;
import com.example.practicalab9.repository.PlayerRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/player")
public class Controller {

    final PlayerRepository playerRepository;

    public Controller(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @GetMapping(value = "/{region}")
    public ResponseEntity<HashMap<String, Object>> listarLeaderBoard(@PathVariable("region") String region) {
        List<Player> listarporRegion = playerRepository.listarLeaderbordXregion(region);
        HashMap<String, Object> responseJson = new HashMap<>();

        if (listarporRegion.isEmpty()) {
            responseJson.put("result", "error");
            responseJson.put("message", "No players found for the specified region");
            return ResponseEntity.badRequest().body(responseJson); // 400 HTTP
        } else {
            responseJson.put("result", "success");
            responseJson.put("message", "SIU");
            responseJson.put("players", listarporRegion);
            return ResponseEntity.ok(responseJson); // 200 HTTP
        }
    }

    @PostMapping("")
    public ResponseEntity<HashMap<String, Object>> addPlayer(
            @RequestBody Player player,
            @RequestParam(value = "fetchId", required = false) boolean fetchId) {

        HashMap<String, Object> responseJson = new HashMap<>();

        playerRepository.save(player);
        updatePositions(player.getRegion());
        if(fetchId){
            responseJson.put("id",player.getId());
        }
        responseJson.put("estado","creado");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseJson);
    }

    private void updatePositions(String region) {
        List<Player> players = playerRepository.findByRegionOrderByMmrDesc(region);
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            player.setPosition(i + 1);
            playerRepository.save(player);
        }
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HashMap<String,String>> gestionException(HttpServletRequest request){
        HashMap<String,String> responseMap = new HashMap<>();
        if(request.getMethod().equals("POST")){
            responseMap.put("estado","error");
            responseMap.put("msg","Debe enviar un producto");
        }
        return ResponseEntity.badRequest().body(responseMap);
    }

   /* @PutMapping(value = "")
    public ResponseEntity<HashMap<String,Object>> actualizarPlayer(@RequestBody Player player) {

        HashMap<String, Object> responseMap = new HashMap<>();

        if (player.getId() != null && player.getId() > 0) {
            Optional<Player> opt = playerRepository.findById(player.getId());
            if (opt.isPresent()) {
                playerRepository.save(player);
                updatePositions(player.getRegion());
                responseMap.put("estado", "actualizado");
                return ResponseEntity.ok(responseMap);
            } else {
                responseMap.put("estado", "error");
                responseMap.put("msg", "El jugador a actualizar no existe");
                return ResponseEntity.badRequest().body(responseMap);
            }
        } else {
            responseMap.put("estado", "error");
            responseMap.put("msg", "Debe enviar un ID");
            return ResponseEntity.badRequest().body(responseMap);
        }
    }*/

    @PutMapping(value = "")
    public ResponseEntity<HashMap<String, Object>> actualizarPlayer(@RequestBody Player player) {
        HashMap<String, Object> responseMap = new HashMap<>();

        if (player.getId() != null && player.getId() > 0) {
            Optional<Player> opt = playerRepository.findById(player.getId());
            if (opt.isPresent()) {
                Player playerFromDb = opt.get();

                // Actualizar solo los campos no nulos
                if (player.getName() != null)
                    playerFromDb.setName(player.getName());
                if (player.getMmr() != 0)
                    playerFromDb.setMmr(player.getMmr());
                if (player.getRegion() != null)
                    playerFromDb.setRegion(player.getRegion());

                // Guardar el jugador actualizado
                playerRepository.save(playerFromDb);

                // Actualizar posiciones después de guardar el jugador
                updatePositions(player.getRegion());

                responseMap.put("estado", "actualizado");
                return ResponseEntity.ok(responseMap);
            } else {
                responseMap.put("estado", "error");
                responseMap.put("msg", "El jugador a actualizar no existe");
                return ResponseEntity.badRequest().body(responseMap);
            }
        } else {
            responseMap.put("estado", "error");
            responseMap.put("msg", "Debe enviar un ID válido");
            return ResponseEntity.badRequest().body(responseMap);
        }
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HashMap<String,String>> gestionExcepcion(HttpServletRequest request) {

        HashMap<String, String> responseMap = new HashMap<>();
        if (request.getMethod().equals("POST") || request.getMethod().equals("PUT")) {
            responseMap.put("estado", "error");
            responseMap.put("msg", "Debe enviar un producto");
        }
        return ResponseEntity.badRequest().body(responseMap);
    }

   /* @DeleteMapping("/{playerId}")
    public ResponseEntity<Void> deletePlayer(@PathVariable("id") int id) {
        HashMap<String, Object> respuesta = new HashMap<>();
        Optional<Player> playerOpt = playerRepository.findById(id);
        Optional<Player> optionalPlayer = playerRepository.findById(id);
        if (optionalPlayer.isPresent()) {
            String region = optionalPlayer.get().getRegion();
            playerRepository.deleteById(id);
            updatePositions(region);
        }
        return ResponseEntity.noContent().build();

    }*/

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<HashMap<String, Object>> borrarProducto(@PathVariable("id") String idStr) {

        HashMap<String, Object> responseMap = new HashMap<>();

        try {
            int id = Integer.parseInt(idStr);
            Optional<Player> optionalPlayer = playerRepository.findById(id);
            if (optionalPlayer.isPresent()) {
                String region = optionalPlayer.get().getRegion();
                playerRepository.deleteById(id);
                updatePositions(region);
                responseMap.put("estado", "borrado exitoso");
                return ResponseEntity.ok(responseMap);
            } else {
                responseMap.put("estado", "error");
                responseMap.put("msg", "no se encontró el producto con id: " + id);
                return ResponseEntity.badRequest().body(responseMap);
            }
        } catch (NumberFormatException ex) {
            responseMap.put("estado", "error");
            responseMap.put("msg", "El ID debe ser un número");
            return ResponseEntity.badRequest().body(responseMap);
        }
    }

}
