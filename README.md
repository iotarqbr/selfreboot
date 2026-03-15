# Self Reboot (Android)

App Android simples para agendar uma tentativa de reboot diário em horário fixo.

## Como funciona

- Você escolhe um horário na tela principal.
- O app agenda um `AlarmManager` exato diário.
- No horário, ele executa `su -c reboot`.
- Após reiniciar o aparelho, o agendamento é restaurado automaticamente via `BOOT_COMPLETED`.

## Limitação importante

O Android **não permite reboot por apps comuns**. Para funcionar de verdade, o dispositivo precisa:

1. estar com **root**, e
2. conceder permissão `su` para o app.

Sem root, o app não consegue reiniciar o sistema e mostrará mensagem de falha.

## Build APK

1. Instale Android Studio (ou SDK + Gradle).
2. Abra esta pasta.
3. Execute:

```bash
./gradlew assembleDebug
```

APK gerado em:

`app/build/outputs/apk/debug/app-debug.apk`

## Observações de Android 12+

- Pode ser necessário conceder permissão para alarmes exatos (`SCHEDULE_EXACT_ALARM`), botão disponível na UI.
