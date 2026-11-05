import 'package:flutter_test/flutter_test.dart';
import 'package:aplicacion_flutter/main.dart';

void main() {
  testWidgets(
    'La pantalla principal muestra la consulta de puntos',
    (WidgetTester tester) async {
      await tester.pumpWidget(
        const PanPuntosPremiosApp(),
      );

      expect(
        find.text('Pan, Puntos y Premios'),
        findsOneWidget,
      );

      expect(
        find.text('Consulta de puntos'),
        findsOneWidget,
      );

      expect(
        find.text('Consultar puntos'),
        findsOneWidget,
      );

      expect(
        find.text('Limpiar'),
        findsOneWidget,
      );
    },
  );
}