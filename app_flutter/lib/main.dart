import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

void main() {
  runApp(const PanPuntosPremiosApp());
}

class PanPuntosPremiosApp extends StatelessWidget {
  const PanPuntosPremiosApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Pan, Puntos y Premios',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: Colors.blue,
        ),
        useMaterial3: true,
      ),
      home: const ConsultaPuntosPage(),
    );
  }
}

class ConsultaPuntosPage extends StatefulWidget {
  const ConsultaPuntosPage({super.key});

  @override
  State<ConsultaPuntosPage> createState() => _ConsultaPuntosPageState();
}

class _ConsultaPuntosPageState extends State<ConsultaPuntosPage> {
  final TextEditingController codigoController = TextEditingController();

  bool cargando = false;

  String? codigoCliente;
  String? nombreCliente;
  int? puntos;
  String? mensaje;

  String get apiUrl {
    String servidor;

    if (kIsWeb) {
      servidor = 'localhost';
    } else if (defaultTargetPlatform == TargetPlatform.android) {
      // En el emulador Android, 10.0.2.2 apunta a la computadora.
      servidor = '10.0.2.2';
    } else {
      servidor = 'localhost';
    }

    return 'http://$servidor:8080/SistemaPedidosFidelizacion/api/puntos';
  }

  @override
  void dispose() {
    codigoController.dispose();
    super.dispose();
  }

  Future<void> consultarPuntos() async {
    final String codigo =
        codigoController.text.trim().toUpperCase();

    if (codigo.isEmpty) {
      setState(() {
        codigoCliente = null;
        nombreCliente = null;
        puntos = null;
        mensaje = 'Ingrese el código del cliente.';
      });

      return;
    }

    setState(() {
      cargando = true;
      codigoCliente = null;
      nombreCliente = null;
      puntos = null;
      mensaje = null;
    });

    try {
      final Uri url = Uri.parse(
        '$apiUrl?codigo=${Uri.encodeQueryComponent(codigo)}',
      );

      final http.Response response =
          await http.get(url);

      final Map<String, dynamic> datos =
          jsonDecode(response.body) as Map<String, dynamic>;

      if (!mounted) {
        return;
      }

      if (response.statusCode == 200) {
        setState(() {
          codigoCliente =
              datos['codigo']?.toString();

          nombreCliente =
              datos['nombre']?.toString();

          puntos =
              datos['puntos'] is int
                  ? datos['puntos'] as int
                  : int.tryParse(
                      datos['puntos'].toString(),
                    );

          mensaje = null;
        });
      } else {
        setState(() {
          mensaje =
              datos['mensaje']?.toString()
              ?? 'No se pudo consultar el saldo.';
        });
      }
    } catch (e) {
      if (!mounted) {
        return;
      }

      setState(() {
        mensaje =
            'No se pudo conectar con el sistema. '
            'Verifique que Tomcat esté activo.';
      });
    } finally {
      if (mounted) {
        setState(() {
          cargando = false;
        });
      }
    }
  }

  void limpiar() {
    codigoController.clear();

    setState(() {
      codigoCliente = null;
      nombreCliente = null;
      puntos = null;
      mensaje = null;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          'Pan, Puntos y Premios',
        ),
        centerTitle: true,
      ),
      body: Center(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24),
          child: ConstrainedBox(
            constraints: const BoxConstraints(
              maxWidth: 500,
            ),
            child: Card(
              elevation: 3,
              child: Padding(
                padding: const EdgeInsets.all(24),
                child: Column(
                  crossAxisAlignment:
                      CrossAxisAlignment.stretch,
                  children: [
                    const Icon(
                      Icons.loyalty,
                      size: 64,
                    ),

                    const SizedBox(height: 16),

                    const Text(
                      'Consulta de puntos',
                      textAlign: TextAlign.center,
                      style: TextStyle(
                        fontSize: 26,
                        fontWeight: FontWeight.bold,
                      ),
                    ),

                    const SizedBox(height: 8),

                    const Text(
                      'Ingrese su código de cliente para consultar su saldo.',
                      textAlign: TextAlign.center,
                    ),

                    const SizedBox(height: 28),

                    TextField(
                      controller: codigoController,
                      textCapitalization:
                          TextCapitalization.characters,
                      decoration: const InputDecoration(
                        labelText: 'Código de cliente',
                        hintText: 'Ejemplo: CLI001',
                        border: OutlineInputBorder(),
                        prefixIcon:
                            Icon(Icons.person_search),
                      ),
                      onSubmitted: (_) {
                        consultarPuntos();
                      },
                    ),

                    const SizedBox(height: 16),

                    FilledButton.icon(
                      onPressed:
                          cargando
                              ? null
                              : consultarPuntos,
                      icon:
                          cargando
                              ? const SizedBox(
                                  width: 20,
                                  height: 20,
                                  child:
                                      CircularProgressIndicator(
                                    strokeWidth: 2,
                                  ),
                                )
                              : const Icon(Icons.search),
                      label: Text(
                        cargando
                            ? 'Consultando...'
                            : 'Consultar puntos',
                      ),
                    ),

                    const SizedBox(height: 8),

                    OutlinedButton(
                      onPressed:
                          cargando
                              ? null
                              : limpiar,
                      child: const Text(
                        'Limpiar',
                      ),
                    ),

                    if (mensaje != null) ...[
                      const SizedBox(height: 24),

                      Container(
                        padding:
                            const EdgeInsets.all(16),
                        decoration: BoxDecoration(
                          color: Theme.of(context)
                              .colorScheme
                              .errorContainer,
                          borderRadius:
                              BorderRadius.circular(12),
                        ),
                        child: Text(
                          mensaje!,
                          textAlign: TextAlign.center,
                          style: TextStyle(
                            color: Theme.of(context)
                                .colorScheme
                                .onErrorContainer,
                          ),
                        ),
                      ),
                    ],

                    if (nombreCliente != null
                        && puntos != null) ...[
                      const SizedBox(height: 24),

                      Container(
                        padding:
                            const EdgeInsets.all(20),
                        decoration: BoxDecoration(
                          color: Theme.of(context)
                              .colorScheme
                              .primaryContainer,
                          borderRadius:
                              BorderRadius.circular(12),
                        ),
                        child: Column(
                          children: [
                            Text(
                              nombreCliente!,
                              textAlign:
                                  TextAlign.center,
                              style:
                                  const TextStyle(
                                fontSize: 22,
                                fontWeight:
                                    FontWeight.bold,
                              ),
                            ),

                            const SizedBox(height: 6),

                            Text(
                              codigoCliente ?? '',
                            ),

                            const SizedBox(height: 20),

                            const Text(
                              'Saldo de puntos',
                            ),

                            const SizedBox(height: 4),

                            Text(
                              '$puntos',
                              style:
                                  const TextStyle(
                                fontSize: 42,
                                fontWeight:
                                    FontWeight.bold,
                              ),
                            ),

                            const Text(
                              'puntos',
                            ),
                          ],
                        ),
                      ),
                    ],
                  ],
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}