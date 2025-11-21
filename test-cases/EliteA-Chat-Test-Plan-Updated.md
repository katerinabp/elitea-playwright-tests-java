# EliteA Chat Application - Comprehensive Test Plan

## Application Overview

EliteA Chat is a sophisticated AI-powered chat application that provides advanced conversation management with multiple AI models and participant types. The application features:

- **Conversation Management**: Create, search, organize conversations into folders
- **Multi-Model Support**: Select from various LLM models (GPT-4o, Claude, etc.)
- **Participants System**: Add users, AI agents, pipelines, toolkits, and MCPs to conversations
- **File Attachments**: Upload and manage files with artifact toolkit integration
- **Real-time Communication**: WebSocket-based real-time messaging with status indicators
- **Message Operations**: Copy, delete, regenerate responses
- **Projects Integration**: Organize conversations within projects
- **Navigation**: Side navigation with quick access to various sections (Agents, Collections, Artifacts, etc.)

## Test Level Definitions
- **Integration**: Tests that verify interaction between multiple components/systems
- **UI**: Tests that verify user interface elements and visual aspects
- **Performance**: Tests that measure response times and system performance
- **Accessibility**: Tests that verify compliance with accessibility standards

## Priority Definitions
- **P0 - Critical**: Core functionality that must work for the application to be usable
- **P1 - High**: Important features that significantly impact user experience
- **P2 - Medium**: Standard features that should work but are not critical
- **P3 - Low**: Nice-to-have features or cosmetic improvements

## Test Scenarios

### 1. Chat Message Functionality

#### 1.1 Send Simple Text Message
**Test Level:** Integration  
**Priority:** P0 - Critical

**Steps:**
1. Navigate to https://next.elitea.ai/alita_ui/chat
2. Verify default greeting message "Hello, Katerina!" is displayed
3. Click in the message input textbox
4. Type a simple message: "Hello, can you help me?"
5. Verify send button becomes enabled
6. Click the "send your question" button

**Expected Results:**
- User message appears in chat history with user avatar and timestamp
- Message shows "less than a minute ago" timing
- AI response appears below user message with "Alita" avatar
- Response includes "Thought for X sec" expandable section
- Message has copy, regenerate, and delete action buttons
- Conversation is automatically named based on message content
- URL updates to include conversation ID and name

#### 1.2 Send Message with Model Selection
**Test Level:** Integration  
**Priority:** P1 - High

**Steps:**
1. Create a new conversation
2. Click on "Select LLM Model" dropdown
3. Verify available models are listed:
   - Claude 4.5 Sonnet
   - Anthropic Claude 3.7 Sonnet
   - Anthropic Claude 4 Sonnet
   - GPT-4.1
   - GPT-4o (we)
   - GPT-4o (default)
   - GPT-4o mini
   - GPT-5
   - GPT-5 mini
   - o4 mini
4. Select "GPT-4o mini" from the list
5. Type message "Test with different model"
6. Send message

**Expected Results:**
- Model selector shows selected model name
- Message sends successfully with selected model
- Response is generated using the selected model
- Model selection persists for the conversation

#### 1.3 Message with Special Characters and Formatting
**Test Level:** Integration  
**Priority:** P2 - Medium

**Steps:**
1. Create new conversation
2. Type message with special characters: "Test @#$%^&* symbols and émojis 🎉"
3. Send message
4. Type message with line breaks (press Shift+Enter)
5. Send message

**Expected Results:**
- Special characters display correctly
- Emojis render properly
- Line breaks are preserved in message display
- No encoding issues or character corruption

#### 1.4 Long Message Handling
**Test Level:** Integration  
**Priority:** P2 - Medium

**Steps:**
1. Create new conversation
2. Type a message exceeding 500 characters
3. Verify message input behavior
4. Send message

**Expected Results:**
- Input field expands to accommodate long text
- No character limit prevents input
- Long message displays correctly in chat history
- Response is generated appropriately

### 2. Conversation Management

#### 2.1 Create New Conversation
**Test Level:** Integration  
**Priority:** P0 - Critical

**Steps:**
1. From any chat page, click "Create Conversation" button in sidebar
2. Verify new conversation is created
3. Check conversations list

**Expected Results:**
- New conversation appears with default greeting
- Conversation shows "Naming" status initially
- After first message, conversation is auto-named
- Conversation appears under "Today" section in sidebar
- URL changes to new conversation ID
- Previous conversation remains in history

#### 2.2 Navigate Between Conversations
**Test Level:** Integration  
**Priority:** P1 - High

**Steps:**
1. Create 3 different conversations with different messages
2. Click on first conversation in sidebar
3. Verify conversation loads
4. Click on second conversation
5. Click on third conversation
6. Use browser back button

**Expected Results:**
- Each conversation loads with complete message history
- Message content is preserved
- Participants list is maintained
- Model selection is remembered per conversation
- Browser back/forward navigation works correctly
- URL updates reflect current conversation

#### 2.3 Search Conversations
**Test Level:** Integration  
**Priority:** P1 - High

**Steps:**
1. Create multiple conversations with distinct topics
2. Click "Search conversations" button
3. Enter search term matching a conversation topic
4. Press Enter or click search

**Expected Results:**
- Search results display matching conversations
- Conversations are filtered based on search term
- Can click result to navigate to conversation
- Clear search returns to full conversation list

#### 2.4 Organize Conversations in Folders
**Test Level:** Integration  
**Priority:** P2 - Medium

**Steps:**
1. Click "Create folder" button in conversations panel
2. Enter folder name
3. Drag conversation into folder
4. Expand/collapse folder
5. Rename folder

**Expected Results:**
- Folder is created successfully
- Conversations can be moved into folders
- Folder shows count of contained conversations
- Folders can be collapsed/expanded
- Conversations remain accessible within folders

### 3. Participants Management

#### 3.1 Add AI Agent to Conversation
**Test Level:** Integration  
**Priority:** P1 - High

**Steps:**
1. Create new conversation
2. Click "Add agent" button in Participants section
3. Verify agent selection dialog appears
4. Use search box to filter agents
5. Select an agent from the list (e.g., "API Testing Buddy")
6. Verify agent is added to participants

**Expected Results:**
- Agent selection dialog displays with search functionality
- Available agents include:
  - API Testing Buddy
  - Business Analyst
  - Code Review Assistant
  - Doc Personal info remover
  - Generate Accessibility Bug examples
  - And many more public agents
- Selected agent appears in participants list
- Agent can be mentioned in messages using @
- Messages can be directed to specific agents

#### 3.2 Add Multiple Participants
**Test Level:** Integration  
**Priority:** P2 - Medium

**Steps:**
1. Create new conversation
2. Add 2-3 different AI agents
3. Send a message
4. Verify which participant responds

**Expected Results:**
- Multiple agents can be added simultaneously
- Participants list shows all added agents
- Can refresh participant lists
- Each participant type (agent, pipeline, toolkit, MCP) has separate section

#### 3.3 Remove Participant
**Test Level:** Integration  
**Priority:** P2 - Medium

**Steps:**
1. In conversation with multiple participants
2. Click remove button next to a participant
3. Confirm removal
4. Send message

**Expected Results:**
- Participant is removed from list
- Removed participant no longer receives messages
- Remaining participants continue to function
- Can re-add removed participant

#### 3.4 Add Pipeline to Conversation
**Test Level:** Integration  
**Priority:** P2 - Medium

**Steps:**
1. Create new conversation
2. Click "Add pipeline" button
3. Select a pipeline from list
4. Verify pipeline is added

**Expected Results:**
- Pipeline selection dialog appears
- Available pipelines are listed
- Selected pipeline appears in participants
- Pipeline can process messages

#### 3.5 Add Toolkit and MCP
**Test Level:** Integration  
**Priority:** P2 - Medium

**Steps:**
1. Create new conversation
2. Click "Add toolkit" button
3. Select a toolkit
4. Click "Add mcp" button
5. Select an MCP

**Expected Results:**
- Toolkits and MCPs can be added to conversation
- Each has separate section in participants panel
- Refresh buttons update available options
- Can add multiple of each type

### 4. File Attachment Functionality

#### 4.1 Attach File to Message
**Test Level:** Integration  
**Priority:** P2 - Medium

**Steps:**
1. Create new conversation
2. Click "attach files" button
3. Verify attachment settings dialog appears
4. Note the artifact toolkit requirement message
5. Click "Cancel" to close dialog

**Expected Results:**
- Attachment settings dialog displays
- Message states: "Choose an artifact toolkit or create new one to keep attached files"
- Artifact toolkit dropdown is available
- Save button is disabled until toolkit is selected
- Can cancel without making changes

#### 4.2 Configure Artifact Toolkit for Attachments
**Test Level:** Integration  
**Priority:** P2 - Medium

**Steps:**
1. Open attachment settings
2. Click on "Attachment's Artifact toolkit" dropdown
3. Select or create an artifact toolkit
4. Click "Save"
5. Attach a file
6. Send message with attachment

**Expected Results:**
- Artifact toolkit can be selected
- Settings are saved
- Files can be attached after configuration
- Attached files appear in message
- Recipients can access attachments

### 5. Message Actions

#### 5.1 Copy Message to Clipboard
**Test Level:** Integration  
**Priority:** P1 - High

**Steps:**
1. Send a message and receive response
2. Hover over AI response
3. Click "Copy to clipboard" button
4. Paste content elsewhere (e.g., notepad)

**Expected Results:**
- Copy button is visible on hover
- Message content is copied to clipboard
- Copied text matches displayed message
- Formatting is preserved where possible

#### 5.2 Delete User Message
**Test Level:** Integration  
**Priority:** P1 - High

**Steps:**
1. Send a message
2. Click "Delete" button on user message
3. Confirm deletion

**Expected Results:**
- Delete button appears on user messages
- Confirmation dialog is shown
- Message is removed from chat history
- Associated AI response handling (kept or removed)
- Conversation history updates

#### 5.3 Regenerate AI Response
**Test Level:** Integration  
**Priority:** P1 - High

**Steps:**
1. Send a message and receive response
2. Click "Regenerate" button on AI response
3. Observe new response generation

**Expected Results:**
- Regenerate button is available on AI messages
- New response is generated
- Previous response is replaced or marked as alternate
- "Thought for X sec" timing is updated
- New response may differ from original

#### 5.4 Expand/Collapse Thought Process
**Test Level:** UI  
**Priority:** P2 - Medium

**Steps:**
1. Send a message that triggers reasoning
2. Click on "Thought for X sec" section
3. Verify thought process details
4. Click again to collapse

**Expected Results:**
- Thought section is expandable/collapsible
- Shows reasoning duration
- May display intermediate thinking steps
- Provides transparency into AI reasoning

### 6. Chat History Management

#### 6.1 Clear Chat History
**Test Level:** Integration  
**Priority:** P1 - High

**Steps:**
1. Create conversation with multiple messages
2. Click "Clear the chat history" button
3. Confirm action
4. Verify chat is cleared

**Expected Results:**
- Clear button is enabled when messages exist
- Confirmation dialog appears
- All messages are removed
- Greeting message reappears
- Participants remain in conversation
- Conversation ID persists

#### 6.2 View Message Timestamps
**Test Level:** UI  
**Priority:** P3 - Low

**Steps:**
1. Send multiple messages with delays between them
2. Observe timestamp updates
3. Check timestamp format

**Expected Results:**
- Timestamps show "less than a minute ago"
- Updates to "X minutes ago"
- Eventually shows "X hours ago"
- Timestamps are accurate
- Timezone handling is correct

### 7. Model Settings and Configuration

#### 7.1 Access Model Settings
**Test Level:** Integration  
**Priority:** P2 - Medium

**Steps:**
1. Click "model settings menu" button (gear icon)
2. Verify settings panel appears
3. Explore available settings
4. Modify a setting
5. Save changes

**Expected Results:**
- Settings panel is accessible
- Configuration options are displayed
- Settings include temperature, max tokens, etc.
- Changes can be saved
- Settings persist for conversation

#### 7.2 Switch Between Chat Modes
**Test Level:** Integration  
**Priority:** P2 - Medium

**Steps:**
1. Click "chatbot" button
2. Verify chat mode options
3. Select different mode
4. Send message

**Expected Results:**
- Different chat modes are available
- Mode selection affects AI behavior
- Visual indication of current mode
- Mode persists in conversation

### 8. Internal Tools

#### 8.1 Enable Internal Tools
**Test Level:** Integration  
**Priority:** P2 - Medium

**Steps:**
1. Create new conversation
2. Click "enable internal tools" button
3. Verify tools are enabled
4. Send message that could use tools
5. Observe tool usage in response

**Expected Results:**
- Internal tools toggle is available
- Visual indication when tools are enabled
- AI can use enabled tools in responses
- Tool usage is indicated in response
- Can disable tools

### 9. Navigation and UI

#### 9.1 Sidebar Navigation
**Test Level:** UI  
**Priority:** P1 - High

**Steps:**
1. Click "open drawer" button
2. Verify sidebar expands
3. Navigate to each section:
   - Chat
   - Agents
   - Pipelines
   - Credentials
   - Toolkits
   - MCP
   - Collections
   - Artifacts
4. Return to Chat

**Expected Results:**
- Sidebar drawer opens/closes
- All navigation items are accessible
- Each section loads correctly
- Active section is highlighted
- Navigation is smooth

#### 9.2 Project Selector
**Test Level:** Integration  
**Priority:** P2 - Medium

**Steps:**
1. Click on "No projects" dropdown
2. Verify project options
3. Select or create a project
4. Verify conversations filter by project

**Expected Results:**
- Project selector is functional
- Can create new projects
- Conversations can be organized by project
- Filter applies to conversation list

#### 9.3 User Profile Menu
**Test Level:** UI  
**Priority:** P2 - Medium

**Steps:**
1. Click on user profile avatar
2. Verify profile menu options
3. Access account settings
4. Logout option (verify presence, don't execute)

**Expected Results:**
- Profile menu displays user information
- Settings are accessible
- Logout option is available
- User avatar displays correctly

#### 9.4 Notifications
**Test Level:** Integration  
**Priority:** P2 - Medium

**Steps:**
1. Click notifications icon
2. Verify notification panel
3. Check for any notifications
4. Mark notifications as read

**Expected Results:**
- Notification panel is accessible
- Unread notifications are indicated
- Notifications can be marked as read
- Notification count updates

#### 9.5 Settings Access
**Test Level:** Integration  
**Priority:** P2 - Medium

**Steps:**
1. Click settings icon (gear)
2. Verify settings panel appears
3. Navigate through settings categories
4. Modify a preference
5. Save changes

**Expected Results:**
- Global settings are accessible
- Various setting categories available
- Changes can be saved
- Settings persist across sessions

### 10. Real-time Features

#### 10.1 Socket Connection Status
**Test Level:** Integration  
**Priority:** P0 - Critical

**Steps:**
1. Observe socket status indicator in sidebar
2. Verify "connected" status
3. Simulate connection loss (if possible)
4. Observe reconnection

**Expected Results:**
- Socket status shows "connected" in green
- Real-time message delivery works
- Connection issues are indicated
- Automatic reconnection occurs
- User is notified of connection status

#### 10.2 Live Message Updates
**Test Level:** Integration  
**Priority:** P0 - Critical

**Steps:**
1. Send a message
2. Observe streaming response
3. Verify real-time updates

**Expected Results:**
- AI responses stream in real-time
- Message appears incrementally
- No page refresh needed
- WebSocket events are logged in console

### 11. Conversation Metadata

#### 11.1 Auto-naming Conversations
**Test Level:** Integration  
**Priority:** P2 - Medium

**Steps:**
1. Create new conversation
2. Observe "Naming" status
3. Send first message
4. Wait for automatic naming
5. Verify conversation name

**Expected Results:**
- Initial status shows "Naming"
- After first message, conversation is named
- Name reflects message content
- Name appears in sidebar
- URL includes conversation name

#### 11.2 Conversation Grouping by Time
**Test Level:** UI  
**Priority:** P3 - Low

**Steps:**
1. Create conversations on different days
2. Verify grouping in sidebar:
   - Today
   - Older

**Expected Results:**
- Conversations grouped by time period
- "Today" section shows current conversations
- "Older" section shows previous conversations
- Groups are collapsible/expandable
- Proper date handling

### 12. Error Handling and Edge Cases

#### 12.1 Send Empty Message
**Test Level:** UI  
**Priority:** P2 - Medium

**Steps:**
1. Click in message input
2. Try to send without typing
3. Type only spaces
4. Try to send

**Expected Results:**
- Send button remains disabled for empty input
- Whitespace-only messages don't send
- No error message needed
- User cannot send invalid messages

#### 12.2 Network Error During Send
**Test Level:** Integration  
**Priority:** P1 - High

**Steps:**
1. Type a message
2. Simulate network disconnection
3. Click send
4. Observe error handling

**Expected Results:**
- Error message is displayed
- Message is not lost
- Can retry sending
- Connection status updates
- Graceful error recovery

#### 12.3 Load Conversation with Large History
**Test Level:** Performance  
**Priority:** P2 - Medium

**Steps:**
1. Access conversation with 50+ messages
2. Observe loading behavior
3. Scroll through history
4. Verify performance

**Expected Results:**
- Conversation loads efficiently
- Virtual scrolling or pagination
- Smooth scrolling performance
- All messages are accessible
- No memory leaks

#### 12.4 Special Character Handling in Search
**Test Level:** Integration  
**Priority:** P1 - High

**Steps:**
1. Create conversation with special chars in name
2. Search using special characters
3. Verify search results

**Expected Results:**
- Special characters are handled correctly
- Search finds matching conversations
- No SQL injection or XSS vulnerabilities
- Proper encoding/decoding

### 13. Accessibility

#### 13.1 Keyboard Navigation
**Test Level:** Accessibility  
**Priority:** P1 - High

**Steps:**
1. Navigate using Tab key
2. Use Enter to send message
3. Use Escape to close dialogs
4. Test arrow keys in dropdowns

**Expected Results:**
- All interactive elements are keyboard accessible
- Tab order is logical
- Enter submits messages
- Escape closes modals
- Focus indicators are visible

#### 13.2 Screen Reader Compatibility
**Test Level:** Accessibility  
**Priority:** P1 - High

**Steps:**
1. Enable screen reader
2. Navigate through chat interface
3. Listen to announcements
4. Send a message

**Expected Results:**
- All elements have proper labels
- ARIA attributes are used correctly
- Messages are announced
- Interactive elements are identified
- Good accessibility tree structure

### 14. Performance

#### 14.1 Message Send Response Time
**Test Level:** Performance  
**Priority:** P1 - High

**Steps:**
1. Send message
2. Measure time to first AI response
3. Note streaming performance

**Expected Results:**
- Message sent within 500ms
- First response token within 3 seconds
- Smooth streaming of response
- No UI freezing

#### 14.2 Conversation Switching Speed
**Test Level:** Performance  
**Priority:** P2 - Medium

**Steps:**
1. Create 10 conversations
2. Switch between them rapidly
3. Measure load times

**Expected Results:**
- Conversation loads within 1 second
- No lag during switching
- Smooth transitions
- Cached data is utilized

## Test Data Requirements

### User Accounts
- Active user account with valid EPAM SSO credentials
- User: katerina_pikulik@epam.com

### Test Messages
- Short messages (< 50 chars)
- Medium messages (50-200 chars)
- Long messages (> 500 chars)
- Messages with special characters
- Messages with emojis
- Multi-line messages

### Test Participants
- At least 3 different AI agents
- At least 1 pipeline
- At least 1 toolkit
- At least 1 MCP

## Environment Configuration

### Test Environment
- Base URL: https://next.elitea.ai/alita_ui/chat
- Browser: Chromium (headless=false for manual verification)
- Viewport: 1920x1080
- Slow motion: 500ms (for visibility during test runs)

### Prerequisites
- Valid authentication credentials
- Network connectivity
- WebSocket support enabled
- Modern browser with JavaScript enabled

## Success Criteria

### Functional Requirements
- All message sending/receiving operations work correctly
- Conversation management features are fully functional
- Participant system works for all participant types
- File attachment system operates as expected
- All UI controls respond appropriately

### Non-Functional Requirements
- Response time < 3 seconds for AI responses
- UI remains responsive during operations
- No JavaScript errors in console
- Proper error handling for all failure scenarios
- Accessibility standards are met (WCAG 2.1 Level AA)

## Known Issues and Limitations

### Console Warnings Observed
- React router blocker warnings (non-blocking)
- Google Analytics initialization messages
- 404 errors for some resources (investigate)
- BSSO Telemetry errors (authentication-related, expected)

### Feature Limitations
- File attachments require artifact toolkit configuration
- Some participants may have limited availability
- Model selection depends on account permissions
- Internal tools availability varies by configuration

## Test Automation Recommendations

### High Priority for Automation (P0-P1)
1. Basic message send/receive (TC 1.1) - P0
2. Create new conversation (TC 2.1) - P0
3. Socket connection verification (TC 10.1) - P0
4. Live message updates (TC 10.2) - P0
5. Navigate between conversations (TC 2.2) - P1
6. Add AI agent to conversation (TC 3.1) - P1
7. Copy message to clipboard (TC 5.1) - P1
8. Delete user message (TC 5.2) - P1
9. Regenerate AI response (TC 5.3) - P1
10. Clear chat history (TC 6.1) - P1

### Medium Priority for Automation (P2)
- Model selection and switching (TC 1.2)
- Special character handling (TC 1.3)
- Long message handling (TC 1.4)
- Search conversations (TC 2.3)
- Organize in folders (TC 2.4)
- Participant management (TC 3.2-3.5)
- File attachments (TC 4.1-4.2)
- Model settings (TC 7.1-7.2)
- Internal tools (TC 8.1)
- Navigation sections (TC 9.1-9.5)

### Low Priority / Manual Testing (P3)
- Message timestamps (TC 6.2)
- Conversation grouping (TC 11.2)
- Accessibility testing (TC 13.1-13.2)
- Performance testing (TC 14.1-14.2)
- Error recovery scenarios
- Visual regression testing
- Browser compatibility testing

## Priority Distribution Summary

| Priority | Count | Percentage |
|----------|-------|------------|
| P0 - Critical | 4 | 10% |
| P1 - High | 14 | 35% |
| P2 - Medium | 20 | 50% |
| P3 - Low | 2 | 5% |
| **Total** | **40** | **100%** |

## Test Level Distribution

| Test Level | Count | Percentage |
|------------|-------|------------|
| Integration | 30 | 75% |
| UI | 6 | 15% |
| Performance | 2 | 5% |
| Accessibility | 2 | 5% |
| **Total** | **40** | **100%** |

## Appendix

### Element Locators Reference

```java
// Message Input
textbox[placeholder*="Type your message"]

// Send Button
button[name="send your question"]

// Create Conversation
button[name="Create Conversation"]

// Add Agent
button[name="Add agent"]

// Model Selector
button[name="Select LLM Model"]

// Clear History
button:has-text("Clear chat history")

// Socket Status
generic[title*="Socket"]

// Copy Message
button[name="Copy to clipboard"]

// Regenerate
button:has-text("Regenerate")

// Delete Message
button[name="Delete"]
```

### Test Execution Notes
- Run tests in incognito mode to ensure clean state
- Monitor WebSocket connections via browser DevTools
- Check console for errors during test execution
- Capture screenshots on failures
- Record test execution for debugging
- Verify database state for critical operations (if accessible)
- Prioritize P0 and P1 tests for smoke/regression suites
- Execute P2 tests in full regression cycles
- P3 tests can be run periodically or manually
