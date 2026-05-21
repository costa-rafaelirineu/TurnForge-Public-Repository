<img width="300" height="700" alt="Screenshot_20260520_162132" src="https://github.com/user-attachments/assets/84ead891-5c52-4c65-aecd-83aa50276162" />
<img width="300" height="700" alt="Screenshot_20260520_163338" src="https://github.com/user-attachments/assets/80fb6785-1c70-4260-90b1-d053746645bd" />
<img width="300" height="700" alt="Screenshot_20260520_162304" src="https://github.com/user-attachments/assets/93560d56-b322-48e5-9c2d-9f0ce728de14" />
<img width="300" height="700" alt="Screenshot_20260520_162231" src="https://github.com/user-attachments/assets/cfab4b66-5825-426f-b960-26aef4537d96" />
<img width="300" height="700" alt="Screenshot_20260520_162224" src="https://github.com/user-attachments/assets/e10214bb-4506-442f-9508-b9af02ce7aa9" />
<img width="300" height="700" alt="Screenshot_20260520_162215" src="https://github.com/user-attachments/assets/fd716aed-1f0a-4014-af87-40a313db1259" />
<img width="300" height="700" alt="Screenshot_20260520_162149" src="https://github.com/user-attachments/assets/107ff232-7ac8-4d82-8345-6d24b434b4c8" />

# TurnForge

TurnForge é um aplicativo mobile multiplataforma (Android e iOS) desenvolvido para auxiliar jogadores de RPG de mesa durante sessões de combate. O aplicativo automatiza cálculos, gerencia turnos e reduz a fricção durante o gameplay, permitindo que jogadores executem ações de combate em até 2 segundos.

## Objetivo

- **Substituir papel e cálculos manuais** durante combates de RPG
- **Reduzir erros** e aumentar a imersão nas sessões
- **Funcionar offline** com backup opcional na nuvem
- **Interface rápida e intuitiva** focada em ação > navegação

## Funcionalidades Principais

### Sistema de Combate

#### Ações de Combate
- **Ataque**: Sistema completo de ataque com presets personalizáveis, cálculo automático de dano e registro de ações
- **Magia**: Sistema de magias com consumo de mana, efeitos variados (incluindo cura), e presets de magias
- **Defesa**: Sistema de defesas ativas com duração, efeitos temporários e gerenciamento de buffs
- **Dados**: Roller de dados completo com suporte a múltiplos dados simultâneos e expressões complexas

#### Motor de Combate (CombatEngine)
- Cálculo automático de dano considerando bônus e penalidades
- Gerenciamento de estado de combate em tempo real
- Sistema de seleção de alvos (inimigos)
- Histórico completo de ações por turno

### Gerenciamento de Personagens

#### Personagem do Jogador
- **Customização completa**: Nome, nível, raça, classe
- **Atributos**: HP atual/máximo, Mana atual/máximo
- **Modo mana infinita**: Opção para personagens sem limite de mana
- **Avatar personalizado**: Sistema de upload e crop de imagens
- **Edição em tempo real**: Modificação rápida de estatísticas durante combate

#### Inimigos
- **Adição dinâmica**: Crie inimigos durante o combate
- **Gerenciamento de HP**: Acompanhe HP atual/máximo de cada inimigo
- **Seleção de alvo**: Sistema de seleção visual para ataques
- **Edição e remoção**: Modifique ou remova inimigos facilmente
- **Ações rápidas**: Sheet de ajuste rápido de HP

### Sistema de Dados

#### DiceEngine
- **Parser de expressões**: Suporte a notação padrão de RPG (ex: 2d6+3, 1d20+5)
- **Múltiplos dados**: Role vários dados simultaneamente
- **Resultados detalhados**: Visualização individual de cada dado rolado
- **Histórico**: Acompanhe rolagens recentes

### Sistema de Status

#### Status Effects
- **Status customizáveis**: Crie efeitos de status personalizados
- **Gerenciamento visual**: Chips coloridos para cada status
- **Edição e remoção**: Modifique ou remova status durante combate
- **Duração**: Sistema de duração para efeitos temporários
- **Defesas ativas**: Gerenciamento separado para defesas com duração

### Sistema de Turnos

#### Controle de Turnos
- **Contador automático**: Acompanhamento automático do número do turno
- **Finalização de turno**: Botão dedicado para encerrar turno atual
- **Limpeza automática**: Reset de estados temporários ao finalizar turno
- **Undo**: Desfazer última ação realizada
- **Histórico completo**: Sheet com histórico de todas as ações por turno

### Persistência de Dados

#### Armazenamento Local (Room Database)
- **Banco de dados local**: Persistência completa usando Room
- **Histórico arquivado**: Sistema de arquivamento de históricos de combate
- **Offline-first**: Funciona completamente sem conexão internet
- **Entities**: TurnEntity, ArchivedHistoryEntity com converters para tipos complexos

#### Backup e Restore
- **Backup na nuvem**: Opção de backup usando Firebase
- **Restore**: Restauração de dados a partir de backup
- **Sincronização**: Data do último backup disponível

### Autenticação

#### Sistema de Login
- **Google Sign-In**: Integração com autenticação Google
- **Gerenciamento de sessão**: Login/logout com persistência
- **Identificação de usuário**: Email do usuário logado
- **Backup vinculado**: Backup associado à conta do usuário

### Interface do Usuário

#### Design
- **Material Design 3**: Interface moderna seguindo guidelines do Material Design
- **Tema customizado**: Esquema de cores e tipografia personalizados
- **Animações**: Transições suaves e feedback visual
- **Haptic feedback**: Feedback tátil em ações importantes
- **Responsive**: Adaptado para diferentes tamanhos de tela

#### Componentes UI
- **CharacterCard**: Card principal do personagem com avatar e estatísticas
- **EnemyCard**: Cards de inimigos com HP e ações
- **ActionGrid**: Grid 2x2 para ações principais (Ataque, Magia, Dados, Defesa)
- **StatusRow**: Linha de status com chips editáveis
- **HpBar**: Barra de HP visual com animações
- **FooterButtons**: Botões de controle de turno (Undo, Histórico, Finalizar)

#### Sheets (Modais)
- **AttackSheet**: Interface para realizar ataques
- **MagicSheet**: Interface para lançar magias
- **DefenseSheet**: Interface para ativar defesas
- **DiceRollerSheet**: Interface para rolar dados
- **StatusSheet**: Interface para gerenciar status
- **CharacterEditSheet**: Interface para editar personagem
- **EnemyEditSheet**: Interface para editar inimigos
- **QuickHpSheet**: Interface para ajuste rápido de HP
- **HistorySheet**: Interface para visualizar histórico
- **AboutSheet**: Interface sobre o app com configurações

### Internacionalização

#### Suporte Multi-idioma
- **Português (pt)**: Interface completa em português
- **Espanhol (es)**: Interface completa em espanhol
- **Inglês (en)**: Interface completa em inglês
- **Strings resources**: Sistema organizado de strings localizadas

### Arquitetura Técnica

#### Stack Tecnológica
- **Kotlin Multiplatform**: Código compartilhado entre Android e iOS
- **Compose Multiplatform**: UI declarativa compartilhada
- **Koin**: Injeção de dependências
- **Room**: Banco de dados local (Android)
- **Firebase**: Autenticação e backup na nuvem
- **Kotlin Coroutines & Flow**: Programação assíncrona reativa

#### Arquitetura em Camadas
- **UI Layer**: Composables, ViewModels, State management
- **Domain Layer**: Use cases, Business logic, Models
- **Data Layer**: Repositories, DAOs, Entities, Converters
- **Platform Layer**: Implementações específicas por plataforma (Android/iOS)

#### Padrões de Projeto
- **MVVM**: Model-View-ViewModel para gerenciamento de estado
- **Repository Pattern**: Abstração de acesso a dados
- **Clean Architecture**: Separação de responsabilidades
- **Dependency Injection**: Injeção de dependências com Koin

## Build e Execução

### Pré-requisitos
- JDK 17 ou superior
- Android Studio (para Android)
- Xcode (para iOS)
- Gradle 8.x

### Build Android

```bash
# Windows
.\gradlew.bat :composeApp:assembleDebug

# macOS/Linux
./gradlew :composeApp:assembleDebug
```

### Build iOS

Abra o diretório `iosApp` no Xcode e execute a partir de lá, ou use as configurações de run do Android Studio.

## Público-Alvo

- **Primário**: Jogadores de RPG de mesa (D&D 5e, Pathfinder, etc.)
- **Secundário**: Mestres de RPG que desejam gerenciar combates

## Roadmap

### Fase 1 (Atual) 
- Combate completo com ações básicas
- Sistema de personagens e inimigos
- Sistema de dados e status
- Persistência local e backup
- Autenticação

### Fase 2 (Planejado)
- Multiplayer em tempo real
- Sincronização entre dispositivos
- Chat integrado

### Fase 3 (Futuro)
- Automação avançada de regras
- Integração com sistemas de RPG específicos
- Modo mestre com ferramentas avançadas

## Licença

Este projeto é desenvolvido como parte do TurnForge.

## Contribuição

Contribuições são bem-vindas! Por favor, abra issues para bugs e feature requests.

## Apoie o Projeto

Se você gostou do TurnForge e quer apoiar o desenvolvimento, considere se tornar um apoiador no Patreon:

[![Patreon](https://img.shields.io/badge/Patreon-F96854?style=for-the-badge&logo=patreon&logoColor=white)](https://patreon.com/RafaelIrineuAndroidEngineer?utm_medium=unknown&utm_source=join_link&utm_campaign=creatorshare_creator&utm_content=copyLink)

---

Desenvolvido com ❤️ usando Kotlin Multiplatform e Compose Multiplatform
