package com.ecapi.serviceImpl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecapi.dto.AuthResponse;
import com.ecapi.dto.LoginRequest;
import com.ecapi.dto.RegisterRequest;
import com.ecapi.model.Rol;
import com.ecapi.model.Usuario;
import com.ecapi.repository.RolRepository;
import com.ecapi.repository.UsuarioRepository;
import com.ecapi.security.CustomUserDetails;
import com.ecapi.security.CustomUserDetailsService;
import com.ecapi.security.JwtUtils;
import com.ecapi.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepo;
    private final RolRepository rolRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (usuarioRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Rol rolCliente = rolRepo.findByNombre("CLIENTE")
                .orElseThrow(() -> new RuntimeException("Rol CLIENTE no existe"));

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApepa(request.getApepa());
        usuario.setApema(request.getApema());
        usuario.setDni(request.getDni());
        usuario.setEmail(request.getEmail());
        usuario.setPwd(passwordEncoder.encode(request.getPwd()));
        usuario.setEstado(1);
        usuario.setRol(rolCliente);
        usuario.setTarjeta(null);

        usuarioRepo.save(usuario);

        UserDetails details = new CustomUserDetails(usuario);

        return new AuthResponse(
                jwtUtils.generateToken(details),
                jwtUtils.generateRefreshToken(details),
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().getNombre()
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPwd()
                )
        );

        UserDetails details = (UserDetails) auth.getPrincipal();
        Usuario usuario = usuarioRepo.findByEmail(details.getUsername()).get();

        return new AuthResponse(
                jwtUtils.generateToken(details),
                jwtUtils.generateRefreshToken(details),
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().getNombre()
        );
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {

        String email = jwtUtils.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!jwtUtils.isTokenValid(refreshToken, userDetails))
            throw new RuntimeException("Refresh token inválido");

        Usuario usuario = usuarioRepo.findByEmail(email).get();

        return new AuthResponse(
                jwtUtils.generateToken(userDetails),
                jwtUtils.generateRefreshToken(userDetails),
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().getNombre()
        );
    }
}
