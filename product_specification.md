## TurnForge – Product Specification (PO Version)

# Product Overview
TurnForge é um aplicativo mobile focado em auxiliar jogadores de RPG de mesa durante o combate, reduzindo fricção, automatizando cálculos e melhorando a experiência em tempo real.

Objetivo principal:
Permitir que jogadores executem ações de combate em até 2 segundos, sem navegação complexa.

Proposta de valor:
- Substituir papel e cálculos manuais
- Reduzir erros durante combate
- Aumentar imersão
- Funcionar offline com backup opcional na nuvem

2. Público-alvo
Primário: Jogadores de RPG (D&D 5e)
Secundário: Mestres de RPG

3. Princípios de Produto
- Offline-first
- Zero fricção inicial
- Ação > Navegação
- Feedback imediato
- UI minimalista em combate

4. Modos do App
Combat Mode: foco total no gameplay
Setup Mode: configurações, personagem, login

5. Funcionalidades MVP
- Ações (ataque, magia, dados, defesa)
- Sistema de status com efeitos automáticos
- Sistema de turnos
- Undo
- Histórico de turnos
- Armazenamento local (Room)
- Backup opcional (Firebase)

6. UX
- Grid de ações 2x2
- Status em chips
- Undo e histórico discretos
- Botão “Finalizar turno” separado

7. Arquitetura
- UI (Compose)
- ViewModel (StateFlow)
- Repository
- Room (local)
- Firebase (nuvem)

8. Métricas
- Tempo por ação < 2s
- Retenção
- Uso de backup

9. Roadmap
Fase 2: multiplayer
Fase 3: automação avançada

10. Diferenciais
- Foco em combate real-time
- UX rápida
- Offline-first

11. Escopo MVP
Inclui:
- Combate completo
- Status
- Turnos
- Undo
- Histórico
- Backup opcional
